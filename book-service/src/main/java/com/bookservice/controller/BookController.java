package com.bookservice.controller;

import com.bookservice.feignclients.CambioFeignClient;
import com.bookservice.model.Book;
import com.bookservice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("book-service")
public class BookController {

    @Autowired
    private Environment environment;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CambioFeignClient cambioFeignClient;

    @GetMapping(value = "/{id}/{currency}")
    public Book findBook(@PathVariable("id") Long id,
                         @PathVariable("currency") String currency) {

        var book = bookRepository.getById(id);
        if (book == null) throw new RuntimeException("Book not found");

        //chamada api externa
        var cambio = cambioFeignClient.getcambio(book.getPrice(),"USD", currency);

        var port = environment.getProperty("local.server.port");
        book.setEnvironment(port + " FEIGN");
        book.setPrice(cambio.getConvertedValue());

        return book;
    }

//    @GetMapping(value = "/{id}/{currency}")
//    public Book findBook(@PathVariable("id") Long id,
//                         @PathVariable("currency") String currency) {
//
//        var book = bookRepository.getById(id);
//        if (book == null) throw new RuntimeException("Book not found");
//
//        HashMap<String, String> params = new HashMap<>();
//        params.put("amoubt", book.getPrice().toString());
//        params.put("from", "USD");
//        params.put("to", currency);
//        var repsonse = new RestTemplate()
//                .getForEntity("http://localhost:8000/cambio-service/{amoubt}/{from}/{to}",
//                Cambio.class,
//                params);
//        var cambio = repsonse.getBody();
//
//        var port = environment.getProperty("local.server.port");
//        book.setEnvironment(port);
//        book.setPrice(cambio.getConvertedValue());
//
//        return book;

//    }
}
