package com.example.empresasapi.application.dtos;

import java.util.List;

public class PaginadoDTO<T> {
    private List<T> datos;
    private int pagina;
    private int tamano;
    private long total;
    private int totalPaginas;

    public PaginadoDTO(List<T> datos, int pagina, int tamano, long total) {
        this.datos = datos;
        this.pagina = pagina;
        this.tamano = tamano;
        this.total = total;
        this.totalPaginas = (int) Math.ceil((double) total / tamano);
    }

    // Getters
    public List<T> getDatos() { return datos; }
    public int getPagina() { return pagina; }
    public int getTamano() { return tamano; }
    public long getTotal() { return total; }
    public int getTotalPaginas() { return totalPaginas; }
}