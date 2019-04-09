package com.ethereum.utils;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.websocket.events.NewHeadsNotification;

public class AccountsUtilsTest {

	AccountUtils utils = new AccountUtils();
	EthereumService web3Service = new EthereumService();

	// @Test
	public void createNewAccountTest() {
		try {
			utils.createNewAccount();
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	// @Test
	public void signMessageTest() {
		try {

			String privateKey = "e300289fefff376895265e7697dfac9ce958a7c78a664346c6e056f30e0bdf3a";
			String publicKey = "30e3941614010defd00e72e1b0ecc7e018a1f50d57619297d02aeabf3f56332089b028567aff39891c646c0e0ba8a9b6d1ca1ca6824aed0d7cffe2112b805390";
			Credentials credentials = Credentials.create(privateKey, publicKey);
			System.out.println(credentials.getAddress());

			String nonce = "1";
			Credentials create = Credentials.create(credentials.getAddress() + nonce);
			System.out.println(create.getEcKeyPair().getPrivateKey().toString(16));
			System.out.println(create.getAddress());

			String message = "abcxyz";
			String signature = utils.signMessage(message, credentials.getEcKeyPair());
			System.out.println(signature);
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	// @Test
	public void getMinerAddressTest() {
		try {

			BigInteger bigInteger = new BigInteger(1, "2".getBytes());
			System.out.println(bigInteger.toString(16));

			String privateKey = "8e5d385b4a50a25395ac2fbd41f6e89cb33df81dd7448d0d4ddc830c15442a4c";
			Credentials credentials = Credentials.create(privateKey);
			EthBlock ethBlock = web3Service.getBlockByNumber("100");
			utils.getMiner(ethBlock.getBlock());
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void ethSubscribeTest() {
		try {

			EthereumService ethereumService = new EthereumService();

			NewHeadConsumer consumer = new NewHeadConsumer();
			ethereumService.ethSubscibe("newHead", NewHeadsNotification.class, consumer);

			Thread.sleep(1000000);

			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

}
