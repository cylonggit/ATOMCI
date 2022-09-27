package org.example.event;

public class CrossTransferEvent extends Event {

  private String from;

  private String to;

  private String tokenId;

  private String invokeId;

  public String getFrom() {
    return from;
  }

  public CrossTransferEvent setFrom(String from) {
    this.from = from;
    return this;
  }

  public String getTo() {
    return to;
  }

  public CrossTransferEvent setTo(String to) {
    this.to = to;
    return this;
  }

  public String getTokenId() {
    return tokenId;
  }

  public CrossTransferEvent setTokenId(String tokenId) {
    this.tokenId = tokenId;
    return this;
  }

  public String getInvokeId() {
    return invokeId;
  }

  public CrossTransferEvent setInvokeId(String invokeId) {
    this.invokeId = invokeId;
    return this;
  }

}
