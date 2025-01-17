package its.incom.webdev.persistence.model;

import java.time.LocalDate;

public class Corso {
    private int id;
    private String nome;
    private Categoria categoria;
    private LocalDate dataInizio;
    private LocalDate dataFine;

    private int n_posti;

    public Corso() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }
    public int getN_posti() {
        return n_posti;
    }

    public void setN_posti(int n_posti) {
        this.n_posti = n_posti;
    }
}
