package com.senai.apivsconnect.controllers;

import com.senai.apivsconnect.dtos.ServicoDto;
import com.senai.apivsconnect.models.ServicoModel;
import com.senai.apivsconnect.repositories.ServicoRepository;
import com.senai.apivsconnect.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Essas anotações indicam que a classe ServicoController é um controlador Spring que
// responde a solicitações HTTP e define o caminho base "/servicos" e o tipo de mídia produzida
// como JSON para todas as operações neste controlador:
@RestController
@RequestMapping(value = "/servicos", produces = {"application/json"})

public class ServicoController {

    //A classe ServicoRepository é injetada para permitir
    // a interação com o banco de dados para operações relacionadas aos serviços:
    @Autowired
    ServicoRepository servicoRepository;

    //O UsuarioRepository é injetado para permitir a busca de informações sobre os usuários:
    @Autowired
    private UsuarioRepository usuarioRepository;

    //@GetMapping - > Este método lida com solicitações HTTP GET para listar todos os serviços.
    // Ele retorna uma resposta que inclui uma lista de todos os serviços no banco de dados:

    @GetMapping
    public ResponseEntity<List<ServicoModel>> listarServicos() {
        return ResponseEntity.status(HttpStatus.OK).body(servicoRepository.findAll());
    }

    //Este método, chamado exibirservico, é responsável por buscar e exibir os detalhes
    // de um serviço com base em um UUID (identificador único) fornecido como parte da URL:
    //Em resumo, este método permite que os clientes da sua aplicação obtenham os detalhes
    // de um serviço com base no seu UUID. Se o serviço for encontrado,
    // ele é retornado com um status "OK" (200).
    // Se o serviço não for encontrado, uma resposta com status "NOT_FOUND" (404)
    // é retornada com uma mensagem indicando que o serviço não foi encontrado.
    @GetMapping("/{idServico}")

    //Este é o cabeçalho do método que define que ele é um controlador que responde a solicitações HTTP GET
    // e espera receber um parâmetro de caminho (path variable) chamado "idServico" do tipo UUID.
    // O método retorna uma resposta encapsulada em uma instância de ResponseEntity,
    // que pode conter um status HTTP e o corpo da resposta:
    public ResponseEntity<Object> exibirservico(@PathVariable(value = "idServico") UUID id) {

        //Aqui, o método tenta buscar um serviço no repositório servicoRepository com base no UUID fornecido.
        // O resultado é armazenado em um Optional, que pode conter o serviço se ele for encontrado ou
        // estar vazio se não for encontrado:
        Optional<ServicoModel> servicoBuscado = servicoRepository.findById(id);

        //Este bloco condicional verifica se o Optional está vazio, o que significa que o serviço não foi encontrado no repositório:
        if (servicoBuscado.isEmpty()) {
            // Se o serviço não for encontrado, o método retorna uma resposta com status
            // HTTP "NOT_FOUND" (404) e uma mensagem no corpo da resposta informando que o serviço não foi encontrado:
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Serviço não encontrado");
        }

        //Se o serviço for encontrado no repositório, o método retorna uma resposta com status
        // HTTP "OK" (200) e o serviço encontrado no corpo da resposta, acessado através do método servicoBuscado.get():
        return ResponseEntity.status(HttpStatus.OK).body(servicoBuscado.get());
    }


    //@PostMapping -> Este método lida com solicitações HTTP POST para cadastrar um novo serviço.
    // Ele recebe um objeto ServicoDto no corpo da solicitação e o converte em um objeto ServicoModel.
    // Em seguida, ele verifica se o cliente associado ao serviço (com base no id_cliente) existe
    // no banco de dados e, se existir, associa o cliente ao serviço. Por fim, o serviço é salvo
    // no banco de dados e uma resposta com status "Created" (201) é retornada, juntamente
    // com os detalhes do serviço cadastrado:
    @PostMapping
    public ResponseEntity<Object> cadastrarServico(@RequestBody @Valid ServicoDto servicoDto) {
        ServicoModel servicoModel = new ServicoModel();

        BeanUtils.copyProperties(servicoDto, servicoModel);

        var cliente = usuarioRepository.findById(servicoDto.id_cliente());

        if (cliente.isPresent()) {
            servicoModel.setCliente(cliente.get());
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id_cliente não encontrado");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(servicoRepository.save(servicoModel));
    }

}
