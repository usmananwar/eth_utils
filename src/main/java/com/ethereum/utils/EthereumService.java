package com.ethereum.utils;

import java.math.BigInteger;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthSubscribe;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketClient;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.protocol.websocket.events.NewHeadsNotification;
import org.web3j.utils.Numeric;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

@SuppressWarnings("rawtypes")
public class EthereumService {
	final static Logger logger = LoggerFactory.getLogger(EthereumService.class);

	final static String ETHEREUM_CLIENT_URL = "http://localhost:8545";
	final static String ETHEREUM_CLIENT_URL_WS = "ws://localhost:8546";

	public BigInteger getTransactionCount(Credentials credentials) throws Exception {
		try {
			EthGetTransactionCount ethGetTransactionCount = getWeb3HttpInstance()
					.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync()
					.get();
			BigInteger nonce = ethGetTransactionCount.getTransactionCount();
			return nonce;
		} catch (Exception e) {
			logger.error("Exception!!!", e);
			throw new Exception(e.getMessage());
		}
	}

	public EthBlock getBlockByNumber(String blockNumber) throws Exception {
		try {
			EthBlock block = getWeb3HttpInstance()
					.ethGetBlockByNumber(new DefaultBlockParameterNumber(new BigInteger(blockNumber)), false)
					.sendAsync().get();

			return block;
		} catch (Exception e) {
			logger.error("Exception!!!", e);
			throw new Exception(e.getMessage());
		}
	}

	public String sendContractTx(String contractAddress, String functionName, List<Type> parameters, BigInteger nonce,
			BigInteger estimatedGas, Credentials credentials) throws Exception {
		RawTransaction rawTransaction = getContractRawTransaction(estimatedGas, nonce, contractAddress, functionName,
				parameters);
		byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
		String hexValue = Numeric.toHexString(signedMessage);
		EthSendTransaction transactionResponse = getWeb3HttpInstance().ethSendRawTransaction(hexValue).send();
		return transactionResponse.getResult();
	}

	public RawTransaction getContractRawTransaction(BigInteger estimatedGas, BigInteger nonce, String contractAddress,
			String methodName, List<Type> parameters) throws Exception {
		Function function = new Function(methodName, parameters, Collections.<TypeReference<?>>emptyList());
		String encodedFunction = FunctionEncoder.encode(function);
		RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, BigInteger.ZERO, estimatedGas,
				contractAddress, encodedFunction);

		return rawTransaction;
	}

	public Credentials getCredentials(String privateKey) {
		Credentials credentials = Credentials.create(privateKey);
		logger.debug("Address: {}", credentials.getAddress());
		return credentials;
	}

	@SuppressWarnings("unchecked")
	public Disposable ethSubscibe(String eventName, Class eventType, Consumer eventConsumer) throws URISyntaxException, ConnectException {

		WebSocketService wsService = getWebSocketService();

		Request<?, EthSubscribe> subscribeRequest = new Request<>(
				// RPC method to subscribe to events
				"eth_subscribe",
				// Parameters that specify what events to subscribe to
				Arrays.asList("newHeads", Collections.emptyMap()),
				// Service that is used to send a request
				wsService, EthSubscribe.class);

		Flowable<NewHeadsNotification> events = wsService.subscribe(subscribeRequest,
				// RPC method that should be used to unsubscribe from events
				"eth_unsubscribe",
				// Type of events returned by a request
				eventType);

		// Subscribe to incoming events and process incoming events
		Disposable disposable = events.subscribe(eventConsumer);
		return disposable;
	}

	public Web3j getWeb3HttpInstance() {
		Web3j web3j = Web3j.build(new HttpService(ETHEREUM_CLIENT_URL));
		return web3j;
	}

	public WebSocketService getWebSocketService() throws URISyntaxException, ConnectException {
		boolean includeRawResponses = false;
		WebSocketClient webSocketClient = new WebSocketClient(new URI(ETHEREUM_CLIENT_URL_WS));
		WebSocketService webSocketService = new WebSocketService(webSocketClient, includeRawResponses);
		webSocketService.connect();
		return webSocketService;
	}

}
