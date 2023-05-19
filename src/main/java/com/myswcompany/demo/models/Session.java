package com.myswcompany.demo.models;
import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "sessions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    //  a String field constrained with @NotBlank must be not null, and the trimmed length must be greater than zero.

    @NotBlank
    @Size(max = 80, message = "session name too long")
    @Column(name = "session_name")
    private String sessionName;


    @Size(max = 1024)
    @Column(name = "session_description")
    private String sessionDescription;


    @Min(5)
    @Max(1000)
    @Column(name = "session_length")
    private Integer sessionLength;

    @ManyToMany
    @JoinTable(
            name = "session_speakers",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "speaker_id"))
    private List<Speaker> speakers;

    public Session()
    { }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<Speaker> speakers) {
        this.speakers = speakers;
    }

    public Long getSession_id() {
        return sessionId;
    }

    public void setSession_id(Long session_id) {
        this.sessionId = session_id;
    }

    public String getSession_name() {
        return sessionName;
    }

    public void setSession_name(String session_name) {
        this.sessionName = session_name;
    }

    public String getSession_description() {
        return sessionDescription;
    }

    public void setSession_description(String session_description) {
        this.sessionDescription = session_description;
    }

    public Integer getSession_length() {
        return sessionLength;
    }

    public void setSession_length(Integer session_length) {
        this.sessionLength = session_length;
    }
}
