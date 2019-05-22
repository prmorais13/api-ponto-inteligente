package com.paulo.pontointeligente.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.paulo.pontointeligente.api.entities.Empresa;
import com.paulo.pontointeligente.api.repositories.EmpresaRepository;
import com.paulo.pontointeligente.api.services.EmpresaService;

public class EmpresaSeviceImpl implements EmpresaService {
	
	private static final Logger log  = LoggerFactory.getLogger(EmpresaSeviceImpl.class);
	
	@Autowired
	private EmpresaRepository empresaRepository;

	@Override
	public Optional<Empresa> buscarPorCnpj(String cnpj) {
		log.info("Buscando uma empresa para o CNPJ {}", cnpj);

		return Optional.ofNullable(empresaRepository.findByCnpj(cnpj));
	}

	@Override
	public Empresa persistir(Empresa empresa) {
		log.info("Persistindo empresa: {}", empresa);
		return this.empresaRepository.save(empresa);
	}

}
