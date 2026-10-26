package io.github.xaxis.rmvconflicts.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false, name = "display_name")
  private String displayName;
  
  @Column(nullable = false)
  private String email;

  protected User() {
  }

  public User(long id) {
    this.id = id;
  }
}
