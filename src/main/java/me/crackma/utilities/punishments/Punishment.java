package me.crackma.utilities.punishments;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class Punishment {

  @Getter
  private final String issuer;
  @Getter
  private final PunishmentType type;
  @Getter
  private final String reason;
  private final long issued;
  private final long expiry;
  @Getter
  @Setter
  private boolean revoked;
  public Punishment(String issuer, PunishmentType type, String reason, long issued, long expiry) {
    this.issuer = issuer;
    this.type = type;
    this.reason = reason;
    this.issued = issued;
    this.expiry = expiry;
    this.revoked = false;
  }
  public Punishment(String fromString) {
    if (fromString.trim().isEmpty());
    String[] punishment = fromString.split(",");
    this.issuer = punishment[0];
    this.type = PunishmentType.valueOf(punishment[1]);
    this.reason = punishment[2];
    this.issued = Long.parseLong(punishment[3]);
    this.expiry = Long.parseLong(punishment[4]);
    this.revoked = Boolean.parseBoolean(punishment[5]);
  }
  public boolean hasExpired() {
    return expiry - new Date().getTime() < 1;
  }
  public String getIssueDate() {
    return new SimpleDateFormat("yyyy/MM/dd").format(new Date(issued));
  }
  public String getExpiryDate() {
    return new SimpleDateFormat("yyyy/MM/dd").format(new Date(expiry));
  }
  public String getFormattedExpiryDate() {
    Duration duration = Duration.ofMillis(expiry - new Date().getTime());
    long days = duration.toDays();
    long hours = duration.toHours() % 24;
    long minutes = duration.toMinutes() % 60;
    long seconds = duration.getSeconds() % 60;
    return days + "d " + hours + "h " + minutes + "m " + seconds + "s";
  }
  public String toString() {
    return issuer + "," + type.name() + "," + reason + "," + issued + "," + expiry + "," + revoked;
  }
}