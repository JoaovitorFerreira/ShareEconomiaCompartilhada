package br.unirio.dsw.compartilhador.api.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import org.springframework.beans.factory.annotation.Autowired;

import br.unirio.dsw.compartilhador.api.controller.CompartilhamentoController;
import br.unirio.dsw.compartilhador.api.controller.CompartilhamentoController.CompartilhamentoDTO;
import br.unirio.dsw.compartilhador.api.controller.CompartilhamentoController.StatusCompartilhamento;
import br.unirio.dsw.compartilhador.api.model.Compartilhamento;
import br.unirio.dsw.compartilhador.api.model.Usuario;
import br.unirio.dsw.compartilhador.api.repository.CompartilhamentoRepository;
import br.unirio.dsw.compartilhador.api.repository.UsuarioRepository;

public class CompartilhamentoUtils 
{
	@Autowired
	private UsuarioRepository usuarioRepositorio;
	
	public static List<CompartilhamentoDTO> organizeByDate(List<CompartilhamentoDTO> compartilhamentos)
	{
		Stack<CompartilhamentoDTO> pilhaAux1 = new Stack<CompartilhamentoDTO>();
		Stack<CompartilhamentoDTO> pilhaAux2 = new Stack<CompartilhamentoDTO>();
		
		compartilhamentos.forEach(c -> {
			LocalDate dataCompNovo = c.getDataInicio();
			
			if(pilhaAux1.empty())
				pilhaAux1.push(c);
			else {
				while(!pilhaAux1.empty()) { 
					CompartilhamentoDTO itemAux1 = pilhaAux1.pop();
					LocalDate dataCompVelho = itemAux1.getDataInicio();
					
					if 
					(
						dataCompNovo.getYear() < dataCompVelho.getYear() 
						||
						(
						dataCompNovo.getYear() == dataCompVelho.getYear() 
						&&
						dataCompNovo.getMonthValue() < dataCompVelho.getMonthValue()
						) 
						||
						(
						dataCompNovo.getYear() == dataCompVelho.getYear() 
						&&
						dataCompNovo.getMonthValue() == dataCompVelho.getMonthValue() 
						&&
						dataCompNovo.getDayOfMonth() < dataCompNovo.getDayOfMonth()
						)
					) 
					{
						pilhaAux2.push(itemAux1);
					} else
					{
						pilhaAux1.push(itemAux1);
						pilhaAux1.push(c);
						break;
					}
					
					if(pilhaAux1.size() == 0)
						pilhaAux2.push(c);
				}
				
				while(!pilhaAux2.empty())
					pilhaAux1.push(pilhaAux2.pop());
			}
		});
		
		while(!pilhaAux1.empty())
			pilhaAux2.push(pilhaAux1.pop());
		
		List<CompartilhamentoDTO> compsDataCrescente = new ArrayList();
		
		while(!pilhaAux2.empty()) {
			compsDataCrescente.add(pilhaAux2.pop());
		}
		
		return compsDataCrescente;
	}
	
	public static boolean validShare(Compartilhamento comp, Long itemId, CompartilhamentoRepository compRepo) {
		List<Compartilhamento> listaCompartilhamentos = compRepo.findByItemId(itemId);
		
		LocalDate dataIniCompNovo = comp.getDataInicio();
		LocalDate dataFimCompNovo = comp.getDataTermino();
		
		if(dataIniCompNovo.isAfter(dataFimCompNovo)) 
			return false;
		
		
		for (int i=0; i < listaCompartilhamentos.size(); i++) {
			Compartilhamento c = listaCompartilhamentos.get(i);
			LocalDate dataIniCompVelho = c.getDataInicio();
			LocalDate dataFimCompVelho = c.getDataTermino();
			
			
			if(
				(dataIniCompNovo.isAfter(dataIniCompVelho) &&
				dataIniCompNovo.isBefore(dataFimCompVelho))
				||
				(dataFimCompNovo.isAfter(dataIniCompVelho) &&
				dataFimCompNovo.isBefore(dataFimCompVelho))
			) 
			{
				return false;
			}
		}
		
		return true;
	}
	

}
