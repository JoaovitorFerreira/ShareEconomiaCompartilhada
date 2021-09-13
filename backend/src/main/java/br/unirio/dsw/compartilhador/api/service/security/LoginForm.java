package br.unirio.dsw.compartilhador.api.service.security;

import lombok.Getter;
import lombok.Setter;

/**
 * Class that represents a login form
 * 
 * @author User
 */
public class LoginForm
{
	private @Getter @Setter String email;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	private @Getter @Setter String senha;
}