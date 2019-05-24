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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paulo.pontointeligente.api.dtos.CadastroPfDto;
import com.paulo.pontointeligente.api.entities.Empresa;
import com.paulo.pontointeligente.api.entities.Funcionario;
import com.paulo.pontointeligente.api.enums.PerfilEnum;
import com.paulo.pontointeligente.api.response.Response;
import com.paulo.pontointeligente.api.services.EmpresaService;
import com.paulo.pontointeligente.api.services.FuncionarioService;
import com.paulo.pontointeligente.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/cadastrar-pf")
@CrossOrigin(origins = "*")
public class CadastroPfController {
	
	private static final Logger log  = LoggerFactory.getLogger(CadastroPfController.class);
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	@Autowired
	private EmpresaService empresaService;

	public CadastroPfController() {
		
	}
	
	@PostMapping
	public ResponseEntity<Response<CadastroPfDto>> cadastrar(@Valid @RequestBody CadastroPfDto cadastroPfDto,
			BindingResult result) {
		
		log.info("Cadastrando PJ: {}", cadastroPfDto.toString());
		Response<CadastroPfDto> response = new Response<CadastroPfDto>();
		
		validarDadosExistentes(cadastroPfDto, result);	
		Funcionario funcionario = this.converterDtoPraFuncionario(cadastroPfDto, result);
		
		if (result.hasErrors()) {
			log.info("Erro validando cadastro PF: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPfDto.getCnpj());
			empresa.ifPresent(emp -> funcionario.setEmpresa(emp));
		this.funcionarioService.persistir(funcionario);
		
		response.setData(this.converterCadastroPfDto(funcionario));
		return ResponseEntity.ok(response);
	}

	private void validarDadosExistentes(CadastroPfDto cadastroPfDto, BindingResult result) {
		Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPfDto.getCnpj());
			if (!empresa.isPresent()) {
				result.addError(new ObjectError("empresa", "Empresa não cadastrada!"));
			}
		
		this.funcionarioService.buscarPorCpf(cadastroPfDto.getCpf())
			.ifPresent(funcinario -> result.addError(new ObjectError("funcionario", "CPF já existente!")));
		
		this.funcionarioService.buscarPorEmail(cadastroPfDto.getEmail())
		.ifPresent(funcinario -> result.addError(new ObjectError("funcionario", "Email já existente!")));
	}
	
	private Funcionario converterDtoPraFuncionario(CadastroPfDto cadastroPfDto, BindingResult result) {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(cadastroPfDto.getNome());
		funcionario.setEmail(cadastroPfDto.getEmail());
		funcionario.setCpf(cadastroPfDto.getCpf());
		funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		
		cadastroPfDto.getQtdHorasAlmoco()
			.ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));
		cadastroPfDto.getQtdHorasTrabalhoDia()
			.ifPresent(qtdHorasTrabalhoDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabalhoDia)));
		cadastroPfDto.getValorHora().ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));			
		
		funcionario.setSenha(PasswordUtils.gerarByCrypt(cadastroPfDto.getSenha()));
		
		return funcionario;
	}
	
	private CadastroPfDto converterCadastroPfDto(Funcionario funcionario) {
		CadastroPfDto cadastroPfDto = new CadastroPfDto();
		
		cadastroPfDto.setId(funcionario.getId());
		cadastroPfDto.setNome(funcionario.getNome());
		cadastroPfDto.setEmail(funcionario.getEmail());
		cadastroPfDto.setCpf(funcionario.getCpf());
		cadastroPfDto.setCnpj(funcionario.getEmpresa().getCnpj());

		funcionario.getQtdHorasAlmocoOpt()
			.ifPresent(qtdHorasAlmoco -> cadastroPfDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
		
		return cadastroPfDto;
	}

}
