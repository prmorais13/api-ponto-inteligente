package com.paulo.pontointeligente.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paulo.pontointeligente.api.entities.Funcionario;
import com.paulo.pontointeligente.api.repositories.FuncionarioRepository;
import com.paulo.pontointeligente.api.services.FuncionarioService;

@Service
public class FuncionarioSeviceImpl implements FuncionarioService {
	
	private static final Logger log  = LoggerFactory.getLogger(FuncionarioSeviceImpl.class);
	
	@Autowired
	private FuncionarioRepository funcionarioRepository;

	@Override
	public Optional<Funcionario> buscarPorCpf(String cpf) {
		log.info("Buscando um funcionario para o CPF {}", cpf);
		return Optional.ofNullable(funcionarioRepository.findByCpf(cpf));
	}

	@Override
	public Optional<Funcionario> buscarPorEmail(String email) {
		log.info("Buscando um funcionario para o EMAIL {}", email);
		return Optional.ofNullable(funcionarioRepository.findByEmail(email));
	}

	@Override
	public Optional<Funcionario> buscarPorId(Long id) {
		log.info("Buscando um funcionario para o ID {}", id);
		return funcionarioRepository.findById(id);
	}

	@Override
	public Funcionario persistir(Funcionario funcionario) {
		log.info("Persistindo funcionario: {}", funcionario);
		return this.funcionarioRepository.save(funcionario);
	}

	

}
