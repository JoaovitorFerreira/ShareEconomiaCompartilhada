package br.unirio.dsw.compartilhador.api.service.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import br.unirio.dsw.compartilhador.api.model.Usuario;
import br.unirio.dsw.compartilhador.api.repository.UsuarioRepository;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter
{
	private static final String AUTH_HEADER = "Authorization";

	private static final String BEARER_PREFIX = "Bearer ";

	@Autowired
	private UsuarioRepository usuarioRepositorio;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException
	{
		String token = request.getHeader(AUTH_HEADER);

		if (token != null && token.startsWith(BEARER_PREFIX))
			token = token.substring(7);
		
		boolean teste = false;
		int teste2 = 0; 
		if(token != null) {
			 teste  = token.isEmpty();
			 teste2 = token.length();
		}
		
		if(token != null && token.length() < 20)
			token = TokenForm.TokenS;

		String username = jwtTokenUtil.getUsernameFromToken(token);

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)
		{
			if (jwtTokenUtil.tokenValido(token))
			{
				Usuario usuario = usuarioRepositorio.findByEmail(username);
				
				if (usuario != null)
				{
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario.getEmail(), null, usuario.getAutorizacoes());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		}

		chain.doFilter(request, response);
	}
}