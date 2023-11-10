package com.senai.apivsconnect.config; //Esta linha declara o pacote ao qual a classe pertence. Pacotes são usados para organizar o código em módulos lógicos.

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//No geral, esse código configura a segurança da aplicação Spring, definindo regras de autorização, políticas de sessão e outros detalhes de segurança.
// O filtro personalizado securityFilter é adicionado para realizar a lógica de segurança personalizada.
@Configuration //Esta anotação indica que a classe é uma configuração Spring, o que significa que ela define configurações para a aplicação.
@EnableWebSecurity // Esta anotação ativa o suporte ao Spring Security na aplicação.
public class SecurityConfig {

    @Autowired //Aqui, uma instância da classe
    SecurityFilter //é injetada na classe
    securityFilter; //

    @Bean
    //Este método cria e configura uma instância de SecurityFilterChain,
    // que é usada para definir a configuração de segurança da aplicação.
    // Ele recebe um objeto HttpSecurity como argumento e retorna a configuração de segurança.
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                // Esta linha desabilita a proteção contra ataques CSRF (Cross-Site Request Forgery),
                // uma medida de segurança que previne solicitações não autorizadas.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //Define a política de criação de sessão como "STATELESS", o que significa que a aplicação
                // não manterá informações de sessão para os usuários, tornando-a mais segura e escalável.
                .authorizeHttpRequests(authorize -> authorize
                        //Esta seção define as regras de autorização para as solicitações HTTP.
                        // No exemplo fornecido, qualquer solicitação é permitida
                        // (permitAll()), o que significa que não há restrições de acesso.
                        // No entanto, há um comentário mencionando uma regra que foi comentada,
                        // que permite solicitações POST para o caminho "/servicos" apenas para usuários
                        // com a função "CLIENTE". Esta regra está desativada no exemplo.
                        .anyRequest().permitAll()
                )
                //Adiciona o filtro securityFilter antes do filtro
                //UsernamePasswordAuthenticationFilter. -> Isso permite que o filtro personalizado securityFilter seja
                // executado antes do filtro padrão de autenticação de nome de usuário e senha.
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    //Este método cria um gerenciador de autenticação, que é necessário para a autenticação de usuários.
    // Ele recebe um objeto AuthenticationConfiguration como argumento e retorna o gerenciador de autenticação.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    //Este método cria um codificador de senha, neste caso
        // um BCryptPasswordEncoder, que é usado para codificar e verificar senhas com segurança.
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
