package org.tron.walletcli.task;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.api.GrpcAPI.AddressPrKeyPairMessage;
import org.tron.sunapi.ErrorCodeEnum;
import org.tron.sunapi.SunNetwork;
import org.tron.sunapi.SunNetworkResponse;
import org.tron.walletcli.config.ConfigInfo;

public class AccountTask extends SideChainTask {

  private static final Logger logger = LoggerFactory.getLogger("AccountTask");

  private List<String> accountList = new ArrayList<>();


  public void runTask(SunNetwork sdk) {
    sendCoinAndDeposit(sdk);
  }

  public void sendCoinAndDeposit(SunNetwork sdk) {

    logger.info("send coin and deposit");

    while (true) {
      sdk.setPrivateKey(ConfigInfo.privateKey);
      accountList.forEach(account -> {
        sdk.getSideChainService().sendCoin(account.split(",")[0], ConfigInfo.contractDepositValue);
        try {
          Thread.sleep(ConfigInfo.interval);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
      accountList.forEach(account -> {
        sdk.setPrivateKey(account.split(",")[1]);
        triggerContract(sdk, ConfigInfo.contractDepositValue, ConfigInfo.contractDeposit);
        try {
          Thread.sleep(ConfigInfo.interval);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void initAccounts (SunNetwork sdk) {

    logger.info("init accounts !");
    sdk.setPrivateKey(ConfigInfo.privateKey);
    try {
      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ConfigInfo.privateKeyAddressFile, true)));
      for (int i = 0; i < ConfigInfo.accountNum; i ++ ) {

        sdk.setPrivateKey(ConfigInfo.privateKey);
        SunNetworkResponse<AddressPrKeyPairMessage> resp = sdk.getMainChainService().generateAddress();
        if (resp.getCode() == ErrorCodeEnum.SUCCESS.getCode()) {
          String info = resp.getData().getAddress() + "," + resp.getData().getPrivateKey();
          sdk.getSideChainService().createAccount(resp.getData().getAddress());
          sdk.getSideChainService().freezeBalance(ConfigInfo.accountFreezeBalance, 3, 1, resp.getData().getAddress());
          accountList.add(info);
          out.write(info);
          out.newLine();
          out.flush();
          try {
            Thread.sleep(10);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      out.close();
    } catch (Exception e) {

    }

  }

}