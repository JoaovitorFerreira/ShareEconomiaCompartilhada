package br.unirio.dsw.compartilhador.api;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CompartilhadorApplication
{
	
	@PostConstruct
    void started() {
      TimeZone.setDefault(TimeZone.getTimeZone("TimeZone"));
    }
	
	public static void main(String[] args)
	{
		SpringApplication.run(CompartilhadorApplication.class, args);
	}
}