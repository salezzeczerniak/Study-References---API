package com.senai.apivsconnect.controllers;

//Descrição do arquivo:
//Em resumo, o controlador LoginController trata solicitações de login,
// autentica as credenciais do usuário usando o AuthenticationManager,
// gera um token JWT com o TokenService e retorna o token como resposta.
// Este é um passo importante na autenticação de usuários em uma aplicação Spring.

import com.senai.apivsconnect.dtos.LoginDto;
import com.senai.apivsconnect.dtos.TokenDto;
import com.senai.apivsconnect.models.UsuarioModel;
import com.senai.apivsconnect.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//Esta anotação indica que a classe LoginController é um controlador Spring que trata as requisições HTTP
// e retorna respostas, geralmente em formato JSON:
@RestController
public class LoginController {

    //O AuthenticationManager é injetado no controlador para permitir a autenticação de usuários.
    // O AuthenticationManager é uma parte essencial do Spring Security e é usado para autenticar as credenciais do usuário:
    @Autowired
    private AuthenticationManager authenticationManager;


    //O serviço TokenService é injetado para permitir a geração e validação de tokens de autenticação.
    // Esse serviço é usado para criar tokens JWT que são usados para autenticar os usuários:
    @Autowired
    private TokenService tokenService;

    //Este método lida com solicitações HTTP POST para o caminho "/login".
    // O método login é acionado quando um cliente envia uma solicitação POST para autenticar um usuário:
    @PostMapping("/login")

    //O método login recebe um objeto LoginDto no corpo da solicitação HTTP.
    // Esse objeto contém as informações de login, como email e senha.
    // O método retorna uma resposta encapsulada em uma instância de ResponseEntity,
    // que pode conter um status HTTP e o corpo da resposta:
    public ResponseEntity<Object> login(@RequestBody @Valid LoginDto dados) {

        //Aqui, um objeto UsernamePasswordAuthenticationToken
        // é criado com as credenciais do usuário (email e senha) fornecidas no LoginDto:
        var usernamePassword = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

        //O AuthenticationManager é usado para autenticar as credenciais fornecidas.
        // Se as credenciais forem válidas, o objeto auth conterá informações sobre o usuário autenticado.
        var auth = authenticationManager.authenticate(usernamePassword);

        //Após a autenticação bem-sucedida, um token de autenticação é gerado usando o serviço tokenService.
        // O objeto auth.getPrincipal() retorna o usuário autenticado,
        // que é convertido em um UsuarioModel para ser usado na geração do token:
        var token = tokenService.gerarToken( (UsuarioModel) auth.getPrincipal() );

        //Uma resposta HTTP de status 200 (OK) é criada e o token gerado
        // é encapsulado em um objeto TokenDto, que é retornado como o corpo da resposta:
        return ResponseEntity.status(HttpStatus.OK).body(new TokenDto(token));
    }
}
