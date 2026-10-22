package com.healthtech.hospital.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MedicoClinicoGeral.class, name = "CLINICO_GERAL"),
        @JsonSubTypes.Type(value = MedicoEspecialista.class, name = "ESPECIALISTA"),
        @JsonSubTypes.Type(value = Cirurgiao.class, name = "CIRURGIAO")
})
public abstract class Profissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    @Column(unique = true)
    private String registroProfissional;

    private String telefone;

    @Email
    private String email;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getRegistroProfissional() { return registroProfissional; }
    public void setRegistroProfissional(String registroProfissional) { this.registroProfissional = registroProfissional; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}