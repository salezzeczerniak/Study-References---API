package com.senai.apivsconnect.config;

import com.senai.apivsconnect.repositories.UsuarioRepository;
import com.senai.apivsconnect.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//Em resumo, este filtro SecurityFilter é responsável por validar tokens de autorização em solicitações HTTP,
// autenticar usuários com base no token e permitir que as solicitações sigam seu fluxo normal após a autenticação.
// Ele lida com casos especiais em que determinadas solicitações não requerem autenticação, como o caminho "/usuarios"
// com método GET.
@Component
// Esta anotação indica que a classe SecurityFilter é um componente gerenciado pelo Spring,
// o que permite a injeção de dependências e o uso em outros componentes.
public class SecurityFilter extends OncePerRequestFilter {
    //A classe SecurityFilter estende OncePerRequestFilter, que é uma classe abstrata do Spring Security
    // que garante que o método doFilterInternal seja executado apenas uma vez por solicitação.
    @Autowired
            //Aqui, a classe TokenService é injetada na classe SecurityFilter.
            // O TokenService é responsável por gerenciar tokens de autenticação na aplicação.
    TokenService tokenService;

    @Autowired
            //O UsuarioRepository é injetado para possibilitar a busca de informações sobre os usuários, como detalhes de autenticação.
    UsuarioRepository usuarioRepository;

    @Override
    // Este método é invocado para cada solicitação HTTP e é onde a lógica de segurança personalizada é implementada.
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Este trecho de código verifica se a solicitação é para o caminho "/usuarios" e é um pedido GET.
        // Se for o caso, a solicitação é permitida sem mais processamento, chamando
        if (request.getRequestURI().equals("/usuarios") && request.getMethod().equals("GET")) {

            filterChain.doFilter(request, response);
            return ;
        }

        //Este trecho de código chama o método receberToken para extrair o token de autorização da solicitação.
        var token = receberToken(request);

        //Se um token estiver presente, a aplicação prossegue para validar o token e estabelecer a autenticação do usuário.
        if (token != null) {
            //O token é validado usando o serviço tokenService,
            // que verifica a autenticidade e a validade do token, retornando o email do usuário associado ao token.
            var email = tokenService.validarToken(token);

            //Com base no email obtido do token, a aplicação busca as informações do
            // usuário no repositório de usuários (usuarioRepository). Isso é usado para criar um objeto
            //UserDetails que representa o usuário autenticado.
            UserDetails usuario = usuarioRepository.findByEmail(email);

            //Um objeto -> var
            //UsernamePasswordAuthenticationToken -> é criado para representar a autenticação do usuário.
            // O primeiro argumento é o usuário, o segundo é a senha (não usada neste caso),
            // e o terceiro são as autoridades (papéis) associadas ao usuário.

            var autenticacao = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

            //A autenticação é definida no contexto de segurança do Spring,
            // permitindo que o usuário autenticado seja reconhecido nas solicitações subsequentes.
            SecurityContextHolder.getContext().setAuthentication(autenticacao);
        }
        //Após a validação do token e a configuração da autenticação,
        // o filtro permite que a solicitação prossiga no fluxo de processamento,
        // chamando filterChain.doFilter(request, response).
        filterChain.doFilter(request, response);
    }

    //Resumindo, o método receberToken verifica se um token de autorização está presente no cabeçalho "Authorization"
    // de uma solicitação HTTP e, se estiver, extrai o token da string, retornando-o para uso posterior na validação
    // da autenticação. Se o cabeçalho "Authorization" não estiver presente, o método retorna null.
    private String receberToken(HttpServletRequest request) {


        //Este trecho obtém o cabeçalho "Authorization" da solicitação HTTP usando o objeto request.
        // O cabeçalho "Authorization" é comumente usado para enviar tokens de autenticação em solicitações HTTP.
        var authHeader = request.getHeader("Authorization");

        //Este bloco condicional verifica se o cabeçalho "Authorization" não está presente na solicitação.
        // Se não estiver presente, o método retorna null, indicando que não há token de autorização na solicitação.
        if (authHeader == null) {
            //Se o cabeçalho "Authorization" estiver presente, este trecho remove a parte "Bearer " do valor do cabeçalho.
            // A string "Bearer " é frequentemente usada para indicar o tipo de autenticação (como um token JWT) e é
            // seguida pelo próprio token. Portanto, esta linha de código remove "Bearer " e retorna apenas o token,
            // que é então usado para validação.
            return null;
        }

        return authHeader.replace("Bearer ", "");
    }
}
