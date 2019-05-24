package com.paulo.pontointeligente.api.controllers;

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

import com.paulo.pontointeligente.api.dtos.CadastroPjDto;
import com.paulo.pontointeligente.api.entities.Empresa;
import com.paulo.pontointeligente.api.entities.Funcionario;
import com.paulo.pontointeligente.api.enums.PerfilEnum;
import com.paulo.pontointeligente.api.response.Response;
import com.paulo.pontointeligente.api.services.EmpresaService;
import com.paulo.pontointeligente.api.services.FuncionarioService;
import com.paulo.pontointeligente.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/cadastrar-pj")
@CrossOrigin(origins = "*")
public class CadastroPjController {
	
	private static final Logger log  = LoggerFactory.getLogger(CadastroPjController.class);
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	@Autowired
	private EmpresaService empresaService;

	public CadastroPjController() {
		
	}
	
	@PostMapping
	public ResponseEntity<Response<CadastroPjDto>> cadastrar(@Valid @RequestBody CadastroPjDto cadastroPjDto,
			BindingResult result) {
		log.info("Cadastrando PJ: {}", cadastroPjDto.toString());
		Response<CadastroPjDto> response = new Response<CadastroPjDto>();
		
		validarDadosExistentes(cadastroPjDto, result);	
		Empresa empresa = this.converterDtoParaEmpresa(cadastroPjDto);
		Funcionario funcionario = this.converterDtoPraFuncionario(cadastroPjDto, result);
		
		if (result.hasErrors()) {
			log.info("Erro validando cadastro PJ: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		this.empresaService.persistir(empresa);
		funcionario.setEmpresa(empresa);
		this.funcionarioService.persistir(funcionario);
		
		response.setData(this.converterCadastroPjDto(funcionario));
		return ResponseEntity.ok(response);
	}

	private void validarDadosExistentes(CadastroPjDto cadastroPjDto, BindingResult result) {
		this.empresaService.buscarPorCnpj(cadastroPjDto.getCnpj())
			.ifPresent(empresa -> result.addError(new ObjectError("empresa", "Empresa já existente!")));
		
		this.funcionarioService.buscarPorCpf(cadastroPjDto.getCpf())
			.ifPresent(funcinario -> result.addError(new ObjectError("funcionario", "CPF já existente!")));
		
		this.funcionarioService.buscarPorEmail(cadastroPjDto.getEmail())
		.ifPresent(funcinario -> result.addError(new ObjectError("funcionario", "Email já existente!")));
	}

	private Empresa converterDtoParaEmpresa(CadastroPjDto cadastroPjDto) {
		Empresa empresa = new Empresa();
		empresa.setCnpj(cadastroPjDto.getCnpj());
		empresa.setRazaoSocial(cadastroPjDto.getRazaoSocial());
		return empresa;
	}
	
	private Funcionario converterDtoPraFuncionario(CadastroPjDto cadastroPjDto, BindingResult result) {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(cadastroPjDto.getNome());
		funcionario.setEmail(cadastroPjDto.getEmail());
		funcionario.setCpf(cadastroPjDto.getCpf());
		funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
		funcionario.setSenha(PasswordUtils.gerarByCrypt(cadastroPjDto.getSenha()));
		return funcionario;
	}
	
	private CadastroPjDto converterCadastroPjDto(Funcionario funcionario) {
		CadastroPjDto cadastroPjDto = new CadastroPjDto();
		cadastroPjDto.setId(funcionario.getId());
		cadastroPjDto.setNome(funcionario.getNome());
		cadastroPjDto.setEmail(funcionario.getEmail());
		cadastroPjDto.setCpf(funcionario.getCpf());
		cadastroPjDto.setRazaoSocial(funcionario.getEmpresa().getRazaoSocial());
		cadastroPjDto.setCnpj(funcionario.getEmpresa().getCnpj());
		
		return cadastroPjDto;
	}

}
