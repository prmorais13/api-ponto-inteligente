package com.paulo.pontointeligente.api.controllers;

import java.math.BigDecimal;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paulo.pontointeligente.api.dtos.FuncionarioDto;
import com.paulo.pontointeligente.api.entities.Funcionario;
import com.paulo.pontointeligente.api.response.Response;
import com.paulo.pontointeligente.api.services.FuncionarioService;
import com.paulo.pontointeligente.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {
	
	private static final Logger log  = LoggerFactory.getLogger(FuncionarioController.class);
	
	@Autowired
	private FuncionarioService funcionarioService;

	public FuncionarioController() {
		
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<FuncionarioDto>> autalizar(@PathVariable("id") Long id,
			@Valid @RequestBody FuncionarioDto funcionarioDto, BindingResult result) {
		log.info("Atualizando funcionario: {}", funcionarioDto.toString());
		Response<FuncionarioDto> response = new Response<FuncionarioDto>();
		
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(id);
		if (!funcionario.isPresent()) {
			result.addError(new ObjectError("funcionario", "Funcionário não encontrado!"));
		}
		
		this.atualizaDadosFuncionario(funcionario.get(), funcionarioDto, result);
		
		if (result.hasErrors()) {
			log.info("Erro validando funcionario: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		this.funcionarioService.persistir(funcionario.get());
		
		response.setData(this.converterFuncionarioDto(funcionario.get()));
		return ResponseEntity.ok(response);
	}
	
	
	private void atualizaDadosFuncionario(Funcionario funcionario, FuncionarioDto funcionarioDto,
			BindingResult result) {
		
		funcionario.setNome(funcionarioDto.getNome());
		
		if (!funcionario.getEmail().equals(funcionarioDto.getEmail())) {
			this.funcionarioService.buscarPorEmail(funcionarioDto.getEmail())
				.ifPresent(func -> result.addError(new ObjectError("email", "Email já existente!")));
			funcionario.setEmail(funcionarioDto.getEmail());
		}
		
		funcionario.setQtdHorasAlmoco(null);
		funcionarioDto.getQtdHorasAlmoco()
			.ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));
		
		funcionario.setQtdHorasTrabalhoDia(null);
		funcionarioDto.getQtdHorasTrabalhoDia()
			.ifPresent(qtdHorasTrabalhadoDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabalhadoDia)));
		
		funcionario.setValorHora(null);
		funcionarioDto.getValorHora()
			.ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));
		
		if (funcionarioDto.getSenha().isPresent()) {
			funcionario.setSenha(PasswordUtils.gerarByCrypt(funcionarioDto.getSenha().get()));;
		}
		
	}

	private FuncionarioDto converterFuncionarioDto(Funcionario funcionario) {
		FuncionarioDto funcionarioDto = new FuncionarioDto();
		funcionarioDto.setId(funcionario.getId());
		funcionarioDto.setNome(funcionario.getNome());
		funcionarioDto.setEmail(funcionario.getEmail());

		funcionario.getQtdHorasAlmocoOpt().ifPresent(
				qtdHorasAlmoco -> funcionarioDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));

		funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(qtdHorasTrabalhoDia -> funcionarioDto
				.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabalhoDia))));

		funcionario.getValorHoraOpt()
				.ifPresent(valorHora -> funcionarioDto.setValorHora(Optional.of(valorHora.toString())));

		return funcionarioDto;
	}

}
