package com.paulo.pontointeligente.api.services;

import java.util.Optional;

import com.paulo.pontointeligente.api.entities.Funcionario;

public interface FuncionarioService {
	
	Optional<Funcionario> buscarPorCpf(String cpf);
	Optional<Funcionario> buscarPorEmail(String email);
	Optional<Funcionario> buscarPorId(Long id);
	Funcionario persistir(Funcionario funcionario);
	

}
