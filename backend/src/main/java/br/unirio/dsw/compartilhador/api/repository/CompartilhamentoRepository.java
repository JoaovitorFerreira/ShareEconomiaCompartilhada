package br.unirio.dsw.compartilhador.api.repository;

import java.util.List;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import br.unirio.dsw.compartilhador.api.model.Compartilhamento;
import br.unirio.dsw.compartilhador.api.model.ItemCompartilhado;

@Transactional(readOnly = true)
@NamedQueries({
	@NamedQuery(name = "CompartilhamentoRepository.bagaca", query = "SELECT ic FROM ItemCompartilhado ic WHERE ic.usuario.id = :ownerId") 
})
public interface CompartilhamentoRepository extends JpaRepository<Compartilhamento, Long>
{
	List<Compartilhamento> findByUsuarioId(Long usuarioId);
	
	Page<Compartilhamento> findByItemId(Long itemId, Pageable pageable);

	List<Compartilhamento> findByItemId(Long itemId);
	
	List<Compartilhamento> findByUsuarioIdAndItemId(Long usuarioId, Long itemId);

//	Page<Compartilhamento> findByUsuarioId(Long usuarioId, Pageable pageable);

	List<Compartilhamento> findByUsuarioIdAndAceito(Long usuarioId, boolean aceito);
	
	@Query("SELECT c FROM Compartilhamento c WHERE c.usuario.id = :ownerId") 
	Page<Compartilhamento> findByUsuarioId(@Param("ownerId") Long ownerId, Pageable pageable);
}