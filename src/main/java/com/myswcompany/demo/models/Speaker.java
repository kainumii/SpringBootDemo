package com.myswcompany.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity(name = "speakers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Speaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "speaker_id")
    // https://stackoverflow.com/questions/23456197/spring-data-jpa-repository-underscore-on-entity-column-name
    private Long speakerId;

    @Column(name = "first_name")
    @Size(min = 2, max = 30, message = "Size of the first_name has to be between 2 - 30 characters")
    @NotBlank
    private String firstName;

    @Size(min = 2, max = 30)
    @NotBlank(message = "Last name is mandatory")
    @Column(name = "last_name")
    private String lastName;

    @Size(min = 2, max = 40)
    @NotBlank
    private String title;

    @Size(min = 2, max = 50)
    @NotBlank
    private String company;

    @Size(max = 2000)
    @Column(name = "speaker_bio")
    private String speakerBio;

    //@Lob
    //@Type(type="org.hibernate.type.BinaryType")
    //private byte[] speaker_photo;

    @ManyToMany(mappedBy = "speakers")
    @JsonIgnore
    private List<Session> sessions;

    public Speaker() {
    }

//    public byte[] getSpeaker_photo() {
//        return speaker_photo;
//    }
//
//    public void setSpeaker_photo(byte[] speaker_photo) {
//        this.speaker_photo = speaker_photo;
//    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    public Long getSpeaker_id() {
        return speakerId;
    }

    public void setSpeaker_id(Long speaker_id) {
        this.speakerId = speaker_id;
    }

    public String getFirst_name() {
        return firstName;
    }

    public void setFirst_name(String first_name) {
        this.firstName = first_name;
    }

    public String getLast_name() {
        return lastName;
    }

    public void setLast_name(String last_name) {
        this.lastName = last_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSpeaker_bio() {
        return speakerBio;
    }

    public void setSpeaker_bio(String speaker_bio) {
        this.speakerBio = speaker_bio;
    }
}
