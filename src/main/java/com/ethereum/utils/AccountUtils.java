package com.ethereum.utils;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.utils.Numeric;

public class AccountUtils {
	private static final Logger logger = LoggerFactory.getLogger(AccountUtils.class);

	static final String MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";

	private byte[] getEthereumMessagePrefix(int messageLength) {
		return MESSAGE_PREFIX.concat(String.valueOf(messageLength)).getBytes();
	}

	private byte[] getEthereumMessageHash(byte[] message) {
		byte[] prefix = getEthereumMessagePrefix(message.length);

		byte[] result = new byte[prefix.length + message.length];
		System.arraycopy(prefix, 0, result, 0, prefix.length);
		System.arraycopy(message, 0, result, prefix.length, message.length);

		return Hash.sha3(result);
	}

	/**
	 * signs a message using a keypair. Signed message can be verfied in smart
	 * contract using 'ecrecover' method of solidity.
	 * 
	 * @param message
	 * @param ecKeyPair
	 * @return
	 * @throws IOException
	 */
	public String signMessage(String message, ECKeyPair ecKeyPair) throws IOException {
		logger.trace("Platform Encoding : " + System.getProperty("file.encoding"));
		String hashh = Numeric.toHexString(message.getBytes());
		logger.trace("Hashs: {}", hashh);

		byte[] hashBytes = getEthereumMessageHash(message.getBytes());
		logger.trace("Message Hash: {}", Numeric.toHexString(hashBytes));
		Sign.SignatureData signatureData = Sign.signMessage(hashBytes, ecKeyPair, false);

		String hexR = Numeric.toHexStringNoPrefix(signatureData.getR());
		String hexS = Numeric.toHexStringNoPrefix(signatureData.getS());
		String hexV = Integer.toString(signatureData.getV(), 16);
		String signature = "0x" + hexR + hexS + hexV;
		logger.trace("R: {}", hexR);
		logger.trace("S: {}", hexS);
		logger.trace("V: {}", hexV);
		logger.trace("Sig: {}", signature);

		return signature;
	}

	public boolean verifySignedMessage(String signedAddressPublickKey, String messageHash,
			Sign.SignatureData signatureData) throws SignatureException {
		String publicKey = Sign.signedMessageToKey(messageHash.getBytes(), signatureData).toString(16);
		if (publicKey.equals(signedAddressPublickKey)) {
			return true;
		}
		return false;
	}

	/**
	 * returns miner's address of a block mined over a blockchain with Clique
	 * consensus algorithm
	 * 
	 * @param block
	 * @return
	 * @throws DecoderException
	 * @throws SignatureException
	 */
	public String getMiner(Block block) throws DecoderException, SignatureException {

		logger.trace("Block Hash: " + block.getHash());
		byte[] buffer = Numeric.hexStringToByteArray(block.getExtraData());
		byte[] extraSeal = Arrays.copyOfRange(buffer, buffer.length - 65, buffer.length);
		byte[] extraVanity = Arrays.copyOfRange(buffer, 0, buffer.length - 65);

		logger.trace("Extra Data: " + block.getExtraData());
		logger.trace("Extra Vanity: " + Hex.toHexString(extraVanity));
		logger.trace("Extra Seal: " + Hex.toHexString(extraSeal));

		BlockHeader header = new BlockHeader(block.getParentHash(), block.getSha3Uncles(), block.getMiner(),
				block.getStateRoot(), block.getTransactionsRoot(), block.getReceiptsRoot(), block.getLogsBloom(),
				block.getDifficultyRaw(), block.getNumberRaw(), block.getGasLimitRaw(), block.getGasUsedRaw(),
				block.getTimestampRaw(), Hex.toHexString(extraVanity), block.getMixHash(), block.getNonceRaw());

		String headerHash = header.getHash();
		String signatureString = Hex.toHexString(extraSeal);
		SignatureData signatureData = decodeSignature(signatureString);

		logger.trace("V: " + signatureData.getV());
		logger.trace("R: " + Hex.toHexString(signatureData.getR()));
		logger.trace("S: " + Hex.toHexString(signatureData.getS()));

		String publicKey = Sign.signedMessageToKey(header.getRlpEncodedHeader(), signatureData).toString(16);

		logger.trace("Header Hash: " + headerHash);
		logger.trace("Extracted Public Key: " + publicKey);
		logger.trace("Miner Address: " + Keys.getAddress(publicKey));
		String address = Hash.sha3(publicKey.replaceAll("0x", ""));
		logger.trace("Miner Address: " + address);
		return address;
	}

	private SignatureData decodeSignature(String signatureHex) {
		byte[] signature = Numeric.hexStringToByteArray(signatureHex);

		byte[] r = new byte[32];
		byte[] s = new byte[32];
		byte v = signature[64];

		if (v == 1)
			v = 28;
		if (v == 0)
			v = 27;

		System.arraycopy(signature, 0, r, 0, 32);
		System.arraycopy(signature, 32, s, 0, 32);

		return new SignatureData(v, r, s);
	}

	/**
	 * creates a new account with a key pair
	 * 
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public void createNewAccount()
			throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
		ECKeyPair ecKeyPair = Keys.createEcKeyPair();
		Credentials credentials = Credentials.create(ecKeyPair);
		logger.trace("Address: {} Private Key: {} Public Key: {}", credentials.getAddress(),
				ecKeyPair.getPrivateKey().toString(16), ecKeyPair.getPublicKey().toString(16));
	}

	public static String compressPubKey(BigInteger pubKey) {
		String pubKeyYPrefix = pubKey.testBit(0) ? "03" : "02";
		String pubKeyHex = pubKey.toString(16);
		String pubKeyX = pubKeyHex.substring(0, 64);
		return pubKeyYPrefix + pubKeyX;
	}

}
