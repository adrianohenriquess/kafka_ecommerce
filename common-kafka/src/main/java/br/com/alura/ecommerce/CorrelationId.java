package br.com.alura.ecommerce;

import java.util.UUID;

public class CorrelationId {

    private final String id;

    public CorrelationId() {
        this.id = UUID.randomUUID().toString();
    }
}
