package com.ethereum.utils;

import org.apache.commons.codec.DecoderException;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.Hash;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.utils.Numeric;

public class BlockHeader {

	byte[] parentHashBytes;
	byte[] sha3Uncles;
	byte[] minerHashBytes;
	byte[] stateRoot;
	byte[] transactionsRoot;
	byte[] receiptsRoot;
	byte[] logsBloom;
	byte[] difficultyRaw;
	byte[] numberRaw;
	byte[] gasLimitRaw;
	byte[] gasUsedRaw;
	byte[] timestampRaw;
	byte[] extraData;
	byte[] mixHash;
	byte[] nonceRaw;

	public BlockHeader(String parentHashBytes, String sha3Uncles, String minerHashBytes, String stateRoot,
			String transactionsRoot, String receiptsRoot, String logsBloom, String difficultyRaw, String numberRaw,
			String gasLimitRaw, String gasUsedRaw, String timestampRaw, String extraData, String mixHash,
			String nonceRaw) throws DecoderException {
		super();
		this.parentHashBytes = getBytes(parentHashBytes);
		this.sha3Uncles = getBytes(sha3Uncles);
		this.minerHashBytes = getBytes(minerHashBytes);
		this.stateRoot = getBytes(stateRoot);
		this.transactionsRoot = getBytes(transactionsRoot);
		this.receiptsRoot = getBytes(receiptsRoot);
		this.logsBloom = getBytes(logsBloom);
		this.difficultyRaw = getBytes(difficultyRaw);
		this.numberRaw = getBytes(numberRaw);
		this.gasLimitRaw = getBytes(gasLimitRaw);
		this.gasUsedRaw = getBytes(gasUsedRaw);
		this.timestampRaw = getBytes(timestampRaw);
		this.extraData = getBytes(extraData);
		this.mixHash = getBytes(mixHash);
		this.nonceRaw = getBytes(nonceRaw);

	}

	public String getHash() {
		return Hex.toHexString(Hash.sha3(getRlpEncodedHeader()));
	}

	public byte[] getRlpEncodedHeader() {
		RlpString rlpParentHashBytes = RlpString.create(parentHashBytes);
		RlpString rlpSha3Uncles = RlpString.create(sha3Uncles);
		RlpString rlpMinerHashBytes = RlpString.create(minerHashBytes);
		RlpString rlpStateRoot = RlpString.create(stateRoot);
		RlpString rlpTransactionsRoot = RlpString.create(transactionsRoot);
		RlpString rlpReceiptsRoot = RlpString.create(receiptsRoot);
		RlpString rlpLogsBloom = RlpString.create(logsBloom);
		RlpString rlpDifficultyRaw = RlpString.create(difficultyRaw);
		RlpString rlpNumberRaw = RlpString.create(numberRaw);
		RlpString rlpGasLimitRaw = RlpString.create(gasLimitRaw);
		RlpString rlpGasUsedRaw = RlpString.create(gasUsedRaw);
		RlpString rlpTimestampRaw = RlpString.create(timestampRaw);
		RlpString rlpExtraData = RlpString.create(extraData);
		RlpString rlpMixHash = RlpString.create(mixHash);
		RlpString rlpNonceRaw = RlpString.create(nonceRaw);

		RlpList rlpList = new RlpList(rlpParentHashBytes, rlpSha3Uncles, rlpMinerHashBytes, rlpStateRoot,
				rlpTransactionsRoot, rlpReceiptsRoot, rlpLogsBloom, rlpDifficultyRaw, rlpNumberRaw, rlpGasLimitRaw,
				rlpGasUsedRaw, rlpTimestampRaw, rlpExtraData, rlpMixHash, rlpNonceRaw);

		byte[] rlpEncodedContent = RlpEncoder.encode(rlpList);
		return rlpEncodedContent;
	}

	private byte[] getBytes(String value) throws DecoderException {
		value = value.replace("0x", "");
		if (org.apache.commons.lang3.StringUtils.isEmpty(value) || "0".equals(value)) {
			return new byte[0];
		}
		return Numeric.hexStringToByteArray(value);
	}

	public void setParentHashBytes(String parentHashBytes) throws DecoderException {
		this.parentHashBytes = getBytes(parentHashBytes);
	}

	public void setSha3Uncles(String sha3Uncles) throws DecoderException {
		this.sha3Uncles = getBytes(sha3Uncles);
	}

	public void setMinerHashBytes(String minerHashBytes) throws DecoderException {
		this.minerHashBytes = getBytes(minerHashBytes);
	}

	public void setStateRoot(String stateRoot) throws DecoderException {
		this.stateRoot = getBytes(stateRoot);
	}

	public void setTransactionsRoot(String transactionsRoot) throws DecoderException {
		this.transactionsRoot = getBytes(transactionsRoot);
	}

	public void setReceiptsRoot(String receiptsRoot) throws DecoderException {
		this.receiptsRoot = getBytes(receiptsRoot);
	}

	public void setLogsBloom(String logsBloom) throws DecoderException {
		this.logsBloom = getBytes(logsBloom);
	}

	public void setDifficultyRaw(String difficultyRaw) throws DecoderException {
		this.difficultyRaw = getBytes(difficultyRaw);
	}

	public void setNumberRaw(String numberRaw) throws DecoderException {
		this.numberRaw = getBytes(numberRaw);
	}

	public void setGasLimitRaw(String gasLimitRaw) throws DecoderException {
		this.gasLimitRaw = getBytes(gasLimitRaw);
	}

	public void setGasUsedRaw(String gasUsedRaw) throws DecoderException {
		this.gasUsedRaw = getBytes(gasUsedRaw);
	}

	public void setTimestampRaw(String timestampRaw) throws DecoderException {
		this.timestampRaw = getBytes(timestampRaw);
	}

	public void setExtraData(String extraData) throws DecoderException {
		this.extraData = getBytes(extraData);
	}

	public void setMixHash(String mixHash) throws DecoderException {
		this.mixHash = getBytes(mixHash);
	}

	public void setNonceRaw(String nonceRaw) throws DecoderException {
		this.nonceRaw = getBytes(nonceRaw);
	}

	public byte[] getParentHashBytes() {
		return parentHashBytes;
	}

	public byte[] getSha3Uncles() {
		return sha3Uncles;
	}

	public byte[] getMinerHashBytes() {
		return minerHashBytes;
	}

	public byte[] getStateRoot() {
		return stateRoot;
	}

	public byte[] getTransactionsRoot() {
		return transactionsRoot;
	}

	public byte[] getReceiptsRoot() {
		return receiptsRoot;
	}

	public byte[] getLogsBloom() {
		return logsBloom;
	}

	public byte[] getDifficultyRaw() {
		return difficultyRaw;
	}

	public byte[] getNumberRaw() {
		return numberRaw;
	}

	public byte[] getGasLimitRaw() {
		return gasLimitRaw;
	}

	public byte[] getGasUsedRaw() {
		return gasUsedRaw;
	}

	public byte[] getTimestampRaw() {
		return timestampRaw;
	}

	public byte[] getExtraData() {
		return extraData;
	}

	public byte[] getMixHash() {
		return mixHash;
	}

	public byte[] getNonceRaw() {
		return nonceRaw;
	}

}
