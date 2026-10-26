package io.github.xaxis.rmvconflicts.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

@Entity
@Table(name = "users")
public class User {

  @Getter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Getter
  @Setter
  @Column(nullable = false, name = "display_name")
  private String displayName;

  @Getter
  @Setter
  @Column(nullable = false)
  private String email;

  protected User() {
  }

  public User(String displayName, String email) {
    this.displayName = displayName.toLowerCase(Locale.ROOT);
    this.email = email.toLowerCase(Locale.ROOT);
  }

}
