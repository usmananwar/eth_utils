package com.ethereum.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;

@SuppressWarnings("rawtypes")
public class EthereumServiceTests {

	final static String CONTRACT_ADDRESS = "0x8688766b999f3afeaeb4d30bf659e25e8eb167f4";

	final static String PRIVATE_KEY1 = "0xac621a89e306297f15a9dd284097ccde7c250398";
	final static String PRIVATE_KEY2 = "0xa9fd9644a4e4a2085e34949305a197f93605db0400924c3adb884732d3296efa";

	EthereumService web3Service = new EthereumService();

	@Test
	public void sendContractBatchTxTest() throws Exception {

		Credentials credentials1 = web3Service.getCredentials(PRIVATE_KEY1);
		Credentials credentials2 = web3Service.getCredentials(PRIVATE_KEY2);
		BigInteger nonce1 = web3Service.getTransactionCount(credentials1);
		BigInteger nonce2 = web3Service.getTransactionCount(credentials2);
		List<Type> parameters = getByte32ParamParams();
		String functionName = "bytes32isEmpty";
		BigInteger estematedGas = new BigInteger("306380");
		int BATCH_SIZE = 5000;
		for (int i = 0; i < BATCH_SIZE; i++) {
			System.out.println(web3Service.sendContractTx(CONTRACT_ADDRESS, functionName, parameters, nonce1,
					estematedGas, credentials1));

			System.out.println(web3Service.sendContractTx(CONTRACT_ADDRESS, functionName, parameters, nonce2,
					estematedGas, credentials2));
			nonce1 = nonce1.add(BigInteger.ONE);
			nonce2 = nonce2.add(BigInteger.ONE);
		}

	}

	@SuppressWarnings("rawtypes")
	public List<Type> getInsertAfltStorIfoParams() throws Exception {
		List<Type> parameters = new ArrayList<Type>();

		parameters.add(new Utf8String(getRandomString(0))); // _afltStorRcgntKey
		parameters.add(new Utf8String(getRandomString(392))); // _afltStorNum
		parameters.add(new Utf8String(getRandomString(6))); // _afltStorBcKey
		parameters.add(new Utf8String(getRandomString(1642))); // _afltStorPbKey
		return parameters;
	}

	@SuppressWarnings("rawtypes")
	public List<Type> getByte32ParamParams() throws Exception {
		List<Type> parameters = new ArrayList<Type>();

		parameters.add(Bytes32.DEFAULT);
		return parameters;
	}

	public String getRandomString(int length) {
		boolean useLetters = true;
		boolean useNumbers = false;
		String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
		return generatedString;

	}

	public String getRandomStr(int index) {
		StringBuffer temp = new StringBuffer();
		Random rnd = new Random();
		for (int i = 0; i < index; i++) {
			int rIndex = rnd.nextInt(3);
			switch (rIndex) {
			case 0:
				// a-z
				temp.append((char) ((int) (rnd.nextInt(26)) + 97));
				break;
			case 1:
				// A-Z
				temp.append((char) ((int) (rnd.nextInt(26)) + 65));
				break;
			case 2:
				// 0-9
				temp.append((rnd.nextInt(10)));
				break;
			}
		}
		return temp.toString();
	}

}
