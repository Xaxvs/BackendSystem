package io.github.xaxis.rmvconflicts.api.exceptions;

public class MembershipNotFoundException extends RuntimeException{

  public MembershipNotFoundException(Long membershipId) {
    super("Membership not found with id: " + membershipId);
  }
}
