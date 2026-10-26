package io.github.xaxis.rmvconflicts.user;

public enum UserStatus {
  IDLE, // has had no claims or activity for a week
  CLAIM_ACTIVE, // has claimed project files within the last week

  ;
}