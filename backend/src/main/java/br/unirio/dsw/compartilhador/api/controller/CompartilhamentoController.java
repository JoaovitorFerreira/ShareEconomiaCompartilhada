package br.unirio.dsw.compartilhador.api.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.unirio.dsw.compartilhador.api.model.Compartilhamento;
import br.unirio.dsw.compartilhador.api.model.ItemCompartilhado;
import br.unirio.dsw.compartilhador.api.model.Usuario;
import br.unirio.dsw.compartilhador.api.repository.CompartilhamentoRepository;
import br.unirio.dsw.compartilhador.api.repository.ItemCompartilhadoRepository;
import br.unirio.dsw.compartilhador.api.repository.UsuarioRepository;
import br.unirio.dsw.compartilhador.api.utils.CompartilhamentoUtils;
import br.unirio.dsw.compartilhador.api.utils.ValidationUtils;
import br.unirio.dsw.compartilhador.api.utils.spring.ControllerResponse;
import br.unirio.dsw.compartilhador.api.utils.spring.ResponseData;
import br.unirio.dsw.compartilhador.api.utils.spring.PageDTO;
import lombok.Data;



@RestController
@RequestMapping("/api/compartilhamento")
@CrossOrigin(origins = "*")
public class CompartilhamentoController {
	
	private static final Logger log = LoggerFactory.getLogger(CompartilhamentoController.class);
	
	@Autowired
	private CompartilhamentoRepository compartilhamentoRepositorio;
	
	@Autowired
	private UsuarioRepository usuarioRepositorio;
	
	@Autowired
	private ItemCompartilhadoRepository itemRepositorio;
	
	private static List<CompartilhamentoDTO>listaCompartilhamentos = null;
	
	
	@GetMapping(value = "/lista")
	public ResponseEntity<ResponseData> list(@RequestParam int page, @RequestParam int per_page, @RequestParam String nomeItem)
	{
		log.info("Listando itens compartilhados");
		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (username == null)
			return ControllerResponse.fail("Não há um usuário logado no sistema.");

        Usuario usuario = usuarioRepositorio.findByEmail(username);

		if (usuario == null)
			return ControllerResponse.fail("Não foi possível recuperar os dados do usuário a partir das credenciais.");
		
		if(nomeItem == null)
			return ControllerResponse.fail("Parâmetro 'nomeItem' não foi definido");
		
		ItemCompartilhado item = itemRepositorio.findByNomeAndUsuarioId(nomeItem, usuario.getId());
		
		if(itemRepositorio == null) 
			return ControllerResponse.fail("Não foi possível recuperar o item a partir das credenciais");
		
		
		List<Compartilhamento> compartilhamentos = compartilhamentoRepositorio.findByItemId(item.getId()); 
		List<CompartilhamentoDTO> compartilhamentosDTO = new ArrayList<CompartilhamentoDTO>();
	
//		if(listaCompartilhamentos != null && 
//			listaCompartilhamentos.size() == compartilhamentos.size())
//			return ControllerResponse.success(listaCompartilhamentos);
//		
		
		compartilhamentos.forEach(c -> {
			CompartilhamentoDTO comp = new CompartilhamentoDTO();
			Optional<Usuario> destinatario = usuarioRepositorio.findById(c.getUsuario().getId());
			comp.setDestinatario(destinatario.get().getNome());
			comp.setDataInicio(c.getDataInicio());
			comp.setDataTermino(c.getDataTermino());
			comp.setId(c.getId());
			
			if(c.isCanceladoDono() || c.isCanceladoUsuario())
				comp.setStatus(StatusCompartilhamento.CANCELADO);
			else if(c.isAceito())
				comp.setStatus(StatusCompartilhamento.ACEITO);
			else if(c.isRejeitado())
				comp.setStatus(StatusCompartilhamento.REJEITADO);
			else 
				comp.setStatus(StatusCompartilhamento.ABERTO);
			
			compartilhamentosDTO.add(comp);
		});
		
		listaCompartilhamentos = CompartilhamentoUtils.organizeByDate(compartilhamentosDTO);
		
		return ControllerResponse.success(listaCompartilhamentos);
	}
	
	@PutMapping(value = "/novo")
	public ResponseEntity<ResponseData> novo(@RequestBody CompartilhamentoForm form, BindingResult result)
	{
		log.info("Criando um novo compartilhamento: {}", form.toString());
		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (username == null)
			return ControllerResponse.fail("nome", "Não há um usuário logado no sistema.");

		Usuario usuario = usuarioRepositorio.findByEmail(username);

		if (usuario == null)
			return ControllerResponse.fail("nome", "Não foi possível recuperar os dados do usuário a partir das credenciais.");
		
		if (form.getNomeItem() == null)
			return ControllerResponse.fail("Nome do Item", "O nome do item compatilhado não foi definido");
		
		ItemCompartilhado item = itemRepositorio.findByNomeAndUsuarioId(form.getNomeItem(), usuario.getId());
		
		if (item == null)
			return ControllerResponse.fail("Item","Item não pode ser recuperado com as informações providas");
		
		if (!ValidationUtils.validEmail(form.getEmailDestinatario())) 
			return ControllerResponse.fail("email", "O e-mail do usuário não está em um formato adequado.");
		
		Usuario destinatario = usuarioRepositorio.findByEmail(form.getEmailDestinatario());
		
		if (destinatario == null) 
			return ControllerResponse.fail("Usuario", "Não foi possível encontrar o usuário com as credenciais fornecidas.");
		
		if (form.getDataInicio() == null)
			return ControllerResponse.fail("Data inicio", "A data de início não foi definida.");
		
		if (form.getDataFim() == null)
			return ControllerResponse.fail("Data fim", "A data de fim não foi definida.");
		
		Compartilhamento c = new Compartilhamento();
		c.setItem(item);
		c.setUsuario(destinatario);
		c.setDataInicio(form.getDataInicio());
		c.setDataTermino(form.getDataFim());
		
		if(!CompartilhamentoUtils.validShare(c, item.getId(), compartilhamentoRepositorio))
			return ControllerResponse.fail("Data definida não é válida");
			
        compartilhamentoRepositorio.save(c);
		return ControllerResponse.success();
	}
	
	@DeleteMapping(value= "dono/cancela/{id}")
	public ResponseEntity<ResponseData> remove(@PathVariable("id") long id)
	{
		log.info("Dono do item cancelando compartilhamento"+ id);
		
		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if(username == null)
			return ControllerResponse.fail("Autenticação", "Usuário não está autenticado");
		
		Usuario usuario = usuarioRepositorio.findByEmail(username);
		
		if(usuario == null)
			return ControllerResponse.fail("Usuário", "Não foi possível recuperar nenhum usuário através dessas crendenciais");
		
		Optional<Compartilhamento> comp = compartilhamentoRepositorio.findById(id);
		
		Optional<ItemCompartilhado> item = itemRepositorio.findById(comp.get().getItem().getId());
		
		if(item.get().getUsuario().getId() != usuario.getId())
			return ControllerResponse.fail("Usuário não tem permissão de cancelar esse compartilhamento, pois item não pertence a ele");
		
		if(!comp.isPresent())
			ControllerResponse.fail("Compartilhamento", "Não foi possível recuperar o compartilhamento através da id fornecida");
		
		if(comp.get().isCanceladoDono() || comp.get().isCanceladoUsuario()) {
			compartilhamentoRepositorio.deleteById(id);
			return ControllerResponse.success();
		}
		
		comp.get().setCanceladoDono(true);
		
		compartilhamentoRepositorio.save(comp.get());
		return ControllerResponse.success();
	}
	
	@GetMapping(value="recebidos/lista")
	public ResponseEntity<ResponseData> listaRecebidos()
	{
		log.info("Listando itens recebidos");
		String username = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (username == null)
			return ControllerResponse.fail("Usuário não se encontra autenticado");
		
		Usuario usuario = usuarioRepositorio.findByEmail(username);
		
		if (usuario == null)
			return ControllerResponse.fail("Não foi possível recuperar o usuário através das credenciais fornecidas");
		
		List<Compartilhamento> itensRecebidos = compartilhamentoRepositorio.findByUsuarioId(usuario.getId());
		
		if (itensRecebidos == null)
			return ControllerResponse.fail("Não foi possível recuperar lista de itens compartilhados com esse usuário ou nenhum iten foi compartilhado com ele até o momento");
		
		List<CompartilhamentoDTO> compsDTO = new ArrayList<CompartilhamentoDTO>();
		
		
		itensRecebidos.forEach(item -> {
			CompartilhamentoDTO comp = new CompartilhamentoDTO();
			String nomeItem = item.getItem().getNome();
			
			comp.setId(item.getId());
			comp.setNomeItem(nomeItem);;
			comp.setDataInicio(item.getDataInicio());
			comp.setDataTermino(item.getDataTermino());
			
			if(item.isCanceladoDono() || item.isCanceladoUsuario())
				comp.setStatus(StatusCompartilhamento.CANCELADO);
			else if(item.isAceito())
				comp.setStatus(StatusCompartilhamento.ACEITO);
			else if(item.isRejeitado())
				comp.setStatus(StatusCompartilhamento.REJEITADO);
			else 
				comp.setStatus(StatusCompartilhamento.ABERTO);
			
			compsDTO.add(comp);
		});
		
		List<CompartilhamentoDTO> compsDTOOrganized = CompartilhamentoUtils.organizeByDate(compsDTO);
		
		return ControllerResponse.success(compsDTOOrganized);
	}
	
	
	@DeleteMapping(value = "rejeita/{id}")
	public ResponseEntity<ResponseData> rejeita(@PathVariable("id") long id) 
	{
		log.info("Rejeitando compartilhamento de item");
		String username = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (username == null)
			return ControllerResponse.fail("Usuário não se encontra autenticado");
		
		Usuario usuario = usuarioRepositorio.findByEmail(username);
		
		if (usuario == null)
			return ControllerResponse.fail("Não foi possível recuperar o usuário através das credenciais fornecidas");
		
		Optional<Compartilhamento> comp = compartilhamentoRepositorio.findById(id);
		
		if(comp == null)
			return ControllerResponse.fail("Compartilhamento não existe");
		
		comp.get().setRejeitado(true);
		compartilhamentoRepositorio.save(comp.get());
		
		return ControllerResponse.success();
	}
	
	//CONCERTARRRRRR ISSOOOOOOOOOOOOOOOOOOO
	@DeleteMapping(value = "aceita/{id}")
	public ResponseEntity<ResponseData> aceita(@PathVariable("id") long id) 
	{
		log.info("Aceitando compartilhamento de item");
		String username = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (username == null)
			return ControllerResponse.fail("Usuário não se encontra autenticado");
		
		Usuario usuario = usuarioRepositorio.findByEmail(username);
		
		if (usuario == null)
			return ControllerResponse.fail("Não foi possível recuperar o usuário através das credenciais fornecidas");
		
		Optional<Compartilhamento> comp = compartilhamentoRepositorio.findById(id);
		
		if(comp == null)
			return ControllerResponse.fail("Compartilhamento não existe");
		
		comp.get().setAceito(true);;
		compartilhamentoRepositorio.save(comp.get());
		
		return ControllerResponse.success();
	}
	
	@GetMapping(value="recebidos/abertos")
	public ResponseEntity<ResponseData> listaRecebidosAbertos()
	{
		log.info("Listando itens recebidos abertos");
		String username = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (username == null)
			return ControllerResponse.fail("Usuário não se encontra autenticado");
		
		Usuario usuario = usuarioRepositorio.findByEmail(username);
		
		if (usuario == null)
			return ControllerResponse.fail("Não foi possível recuperar o usuário através das credenciais fornecidas");
		
		List<Compartilhamento> itensRecebidos = compartilhamentoRepositorio.findByUsuarioId(usuario.getId());
		
		if (itensRecebidos == null)
			return ControllerResponse.fail("Não foi possível recuperar lista de itens compartilhados com esse usuário ou nenhum iten foi compartilhado com ele até o momento");
		
		List<CompartilhamentoDTO> compsDTO = new ArrayList<CompartilhamentoDTO>();
		
		
		itensRecebidos.forEach(item -> {
			CompartilhamentoDTO comp = new CompartilhamentoDTO();
			String nomeItem = item.getItem().getNome();
			
			comp.setId(item.getId());
			comp.setNomeItem(nomeItem);;
			comp.setDataInicio(item.getDataInicio());
			comp.setDataTermino(item.getDataTermino());
			
			if(item.isCanceladoDono() || item.isCanceladoUsuario())
				comp.setStatus(StatusCompartilhamento.CANCELADO);
			else if(item.isAceito())
				comp.setStatus(StatusCompartilhamento.ACEITO);
			else if(item.isRejeitado())
				comp.setStatus(StatusCompartilhamento.REJEITADO);
			else 
				comp.setStatus(StatusCompartilhamento.ABERTO);
			
			if(comp.getStatus().equals(StatusCompartilhamento.ABERTO))
				compsDTO.add(comp);
		});
		
		List<CompartilhamentoDTO> compsDTOOrganized = CompartilhamentoUtils.organizeByDate(compsDTO);
		
		return ControllerResponse.success(compsDTOOrganized);
		
	}
	
	
	
	
	/**
	 * Formulário para registro de um novo compartilhamento
	 * 
	 * @author User
	 */
	@Data static class CompartilhamentoForm
	{
		private String nomeItem;
		
		public String getNomeItem() {
			return nomeItem;
		}

		public void setNomeItem(String nomeItem) {
			this.nomeItem = nomeItem;
		}

		public String getEmailDestinatario() {
			return emailDestinatario;
		}

		public void setEmailDestinatario(String emailDestinatario) {
			this.emailDestinatario = emailDestinatario;
		}

		public LocalDate getDataInicio() {
			return dataInicio;
		}

		public void setDataInicio(LocalDate dataInicio) {
			this.dataInicio = dataInicio;
		}

		public LocalDate getDataFim() {
			return dataFim;
		}

		public void setDataFim(LocalDate dataFim) {
			this.dataFim = dataFim;
		}

		private String emailDestinatario;
		
		private LocalDate dataInicio;
		
		private LocalDate dataFim;
	}
	
	/**
	 * Classe que representa um compartilhamento para o lado cliente 
	 * 
	 * @author User
	 */
	@Data public class CompartilhamentoDTO
	{
		private long id;
		
		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getDestinatario() {
			return destinatario;
		}

		public void setDestinatario(String destinatario) {
			this.destinatario = destinatario;
		}

		public String getNomeItem() {
			return nomeItem;
		}

		public void setNomeItem(String nomeItem) {
			this.nomeItem = nomeItem;
		}

		public LocalDate getDataInicio() {
			return dataInicio;
		}

		public void setDataInicio(LocalDate dataInicio) {
			this.dataInicio = dataInicio;
		}

		public LocalDate getDataTermino() {
			return dataTermino;
		}

		public void setDataTermino(LocalDate dataTermino) {
			this.dataTermino = dataTermino;
		}

		public StatusCompartilhamento getStatus() {
			return status;
		}

		public void setStatus(StatusCompartilhamento status) {
			this.status = status;
		}

		private String destinatario;
		
		private String nomeItem;
		
		private LocalDate dataInicio;
		
		private LocalDate dataTermino;
		
		private StatusCompartilhamento status;
	}
	
	
	
	public enum StatusCompartilhamento
	{
		ABERTO,
		ACEITO,
		REJEITADO,
		CANCELADO
	}

}
