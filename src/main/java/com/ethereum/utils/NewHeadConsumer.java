package com.ethereum.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.websocket.events.NewHead;
import org.web3j.protocol.websocket.events.NewHeadsNotification;
import org.web3j.protocol.websocket.events.NotificationParams;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.reactivex.functions.Consumer;

public class NewHeadConsumer implements Consumer<NewHeadsNotification> {

	private static final Logger logger = LoggerFactory.getLogger(NewHeadConsumer.class);

	AccountUtils utils;
	EthereumService ethereumService;
	ObjectMapper mapper;

	public NewHeadConsumer() {
		this.utils = new AccountUtils();
		this.ethereumService = new EthereumService();
		this.mapper = new ObjectMapper();
	}

	@Override
	public void accept(NewHeadsNotification t) throws Exception {

		NotificationParams<NewHead> params = t.getParams();
		NewHead newHead = params.getResult();
		logger.debug("NewHead detected {}", newHead.getNumber());

		EthBlock ethBlock = ethereumService.getBlockByNumber(String.valueOf(Integer.decode(newHead.getNumber())));
		ethBlock.getBlock().setAuthor(utils.getMiner(ethBlock.getBlock()));
		logger.debug(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ethBlock.getBlock()));

	}

}
