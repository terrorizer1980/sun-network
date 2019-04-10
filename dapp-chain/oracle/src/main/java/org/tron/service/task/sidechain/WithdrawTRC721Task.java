package org.tron.service.task.sidechain;

import java.math.BigInteger;
import lombok.extern.slf4j.Slf4j;
import org.tron.client.MainChainGatewayApi;
import org.tron.client.SideChainGatewayApi;
import org.tron.service.task.EventTask;

@Slf4j(topic = "sideChainTask")
public class WithdrawTRC721Task implements EventTask {

  // "event WithdrawTRC721(address from, uint256 tokenId, address mainChainAddress, bytes memory txData);"

  private String from;
  private String tokenId;
  private String mainChainAddress;
  private String txData;

  public WithdrawTRC721Task(String from, String tokenId, String mainChainAddress, String txData) {
    this.from = from;
    this.tokenId = tokenId;
    this.mainChainAddress = mainChainAddress;
    this.txData = txData;
  }

  @Override
  public void run() {
    logger.info("from: {}, tokenId: {}, mainChainAddress: {}, txData: {}", this.from, this.tokenId,
      this.mainChainAddress, this.txData);
    try {
      String txId = MainChainGatewayApi
        .withdrawTRC721(this.from, this.mainChainAddress, this.tokenId, this.txData);
      MainChainGatewayApi.checkTxInfo(txId);
    } catch (Exception e) {
      logger
        .error("WithdrawTRC721Task fail, from: {}, tokenId: {}, mainChainAddress: {}, txData: {}",
          this.from, this.tokenId, this.mainChainAddress, this.txData);
    }
  }
}