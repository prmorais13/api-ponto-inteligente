package com.paulo.pontointeligente.api.controllers;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paulo.pontointeligente.api.dtos.LancamentoDto;
import com.paulo.pontointeligente.api.entities.Lancamento;
import com.paulo.pontointeligente.api.response.Response;
import com.paulo.pontointeligente.api.services.FuncionarioService;
import com.paulo.pontointeligente.api.services.LancamentoService;

@RestController
@RequestMapping("/api/lancamentos")
@CrossOrigin(origins = "*")
public class LancamentoController {
	
	private static final Logger log  = LoggerFactory.getLogger(LancamentoController.class);
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Value("${paginacao.qtd_por_pagina}")
	private int qtdPorPagina;

	public LancamentoController() {
		
	}
	
	@GetMapping(value = "/funcionario/{funcionarioId}")
	public ResponseEntity<Response<Page<LancamentoDto>>> listarPorFuncionarioId(
		@PathVariable("funcionarioId") Long funcionarioId,
		@RequestParam(value = "pag", defaultValue = "0") int pag,
		@RequestParam(value = "ord", defaultValue = "id") String ord,
		@RequestParam(value = "dir", defaultValue = "DESC") String dir) {
		
			log.info("Buscando lancamentos por ID do funcionário: {}, página: {}", funcionarioId, pag);
			Response<LancamentoDto> response = new Response<LancamentoDto>();
			
			PageRequest pageRequest = PageRequest.of(pag, this.qtdPorPagina, Direction.valueOf(dir), ord);
			Page<Lancamento> lancamentos = this.lancamentoService.buscarPorFuncionarioId(funcionarioId, pageRequest);
			Page<LancamentoDto> lancamentosDto = lancamentos.map(lancamento -> this.converteLancamentoDto(lancamento));
		
			response.setData(lancamentosDto);
			return ResponseEntity.ok(response);
		}

	private Page<Lancamento> converteLancamentoDto(Lancamento lancamento) {
		LancamentoDto lancamentoDto = new LancamentoDto();
		lancamentoDto.setId(Optional.of(lancamento.getId()));
		lancamentoDto.setData(this.dateFormat.format(lancamento.getData()));
		lancamentoDto.setTipo(lancamento.getTipo().toString());
		lancamentoDto.setDescricao(lancamento.getDescricao());
		lancamentoDto.setLocalizacao(lancamento.getLocalizacao());
		lancamentoDto.setFuncionarioId(lancamento.getFuncionario().getId());
		
		return lancamentoDto;
	}
	}
	
	/**
	 * @param cadastroPjDto
	 * @param result
	 * @return
	 */
	/*@PostMapping
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
*/
