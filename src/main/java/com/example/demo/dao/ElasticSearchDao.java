package com.example.demo.dao;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.vo.InputObject;
import com.example.demo.vo.ResponseObject;

@Configuration
public class ElasticSearchDao {
	private static TransportClient client;

	@Bean
	public ElasticSearchDao elasticBean() {
		return new ElasticSearchDao();
	}

	public static TransportClient getElasticClient() throws UnknownHostException {
		System.setProperty("es.path.home", "D:\\target\\elasticsearch-6.1.2\\elasticsearch-6.1.2\\bin");
		if (client == null) {
			// node client
			/*
			 * Node node =
			 * nodeBuilder().clusterName("elasticsearch").client(true).node();
			 * client = node.client(); return client;
			 */
			// transport client
			client = new PreBuiltTransportClient(Settings.EMPTY)
					.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300))
					.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));

			return client;
		} else
			return client;
	}

	public Object AddDocument(String input) throws UnknownHostException {
		ResponseObject responseObject = new ResponseObject();
		InputObject obj = processInput(input);
		if (!obj.isMalformed()) {
			TransportClient client = getElasticClient();
			IndexResponse response = null;
			try {
				response = client.prepareIndex(obj.getIndex(), obj.getIndex(), obj.getId())
						.setSource(obj.getData(), XContentType.JSON).get();
				responseObject.setSuccessMessage(response.getResult().name());
				responseObject.setId(response.getId());
				responseObject.setData(obj.getData());

			} catch (Exception ex) {
				System.out.println(ex.getMessage());
				responseObject.setErrorMessage("Exception In creation of Entity");
				return responseObject;
			}
			return responseObject;
		} else {
			responseObject.setErrorMessage("Malformed input json");
		}
		return responseObject;
	}

	public Object getSingalDocument(String index, String id) {
		ResponseObject responseObject = new ResponseObject();
		TransportClient client = null;
		GetResponse response = null;
		String docType = index;
		try {
			client = getElasticClient();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObject.setErrorMessage("Exception In getting Elastic Transport Client");
			return responseObject;
		}
		// client.admin().indices().prepareRefresh().execute().actionGet();
		try {
			response = client.prepareGet(index, docType, id).get();
			if(response.getSourceAsString()==null ||response.getSourceAsString()=="")
			responseObject.setSuccessMessage("Not Found");
			else
				responseObject.setSuccessMessage("Fetched");
			responseObject.setId(response.getId());
			responseObject.setData(response.getSourceAsString());
		} catch (Exception ex) {
			responseObject.setErrorMessage("Exception In getting Elastic Data");
			return responseObject;
		}
		return responseObject;
	}

	public Object getAllDocments(String index) {
		ResponseObject responseObject = new ResponseObject();
		TransportClient client = null;
		SearchResponse response = null;
		List<Map<String, Map<String, Object>>> esData;
		Object obj = null;
		try {
			client = getElasticClient();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObject.setErrorMessage("Exception In getting Elastic Transport Client");
			return responseObject;
		}
		// client.admin().indices().prepareRefresh().execute().actionGet();
		try {
			/*
			 * response = client.prepareSearch(index).get(); obj=response.ge;
			 */
			int scrollSize = 1000;
			esData = new ArrayList<Map<String, Map<String, Object>>>();
			int i = 0;
			while (response == null || response.getHits().getHits().length != 0) {
				response = client.prepareSearch(index).setTypes(index).setQuery(QueryBuilders.matchAllQuery())
						.setSize(scrollSize).setFrom(i * scrollSize).execute().actionGet();
				for (SearchHit hit : response.getHits()) {
					Map<String, Map<String, Object>> temp = new HashMap<String, Map<String, Object>>();
					temp.put(hit.getId(), hit.getSourceAsMap());
					esData.add(temp);
				}
				i++;
			}
			return esData;
		} catch (Exception ex) {
			responseObject.setErrorMessage("Exception In getting Elastic Data");
			return responseObject;
		}

	}

	public Object deleteDocument(String index, String id) {
		TransportClient client = null;
		ResponseObject responseObject = new ResponseObject();
		try {
			client = getElasticClient();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			responseObject.setErrorMessage("Exception In getting Elastic Transport Client");
			return responseObject;
		}
		try {
			DeleteResponse response = client.prepareDelete(index, index, id).get();
			responseObject.setSuccessMessage(response.getResult().name());
			return responseObject;
		} catch (Exception ex) {
			responseObject.setErrorMessage("Exception In getting Elastic Data");
			return responseObject;
		}

	}

	public Object updateDocment(String input) {
		ResponseObject responseObject = new ResponseObject();
		InputObject inputObject = processInput(input);
		TransportClient client = null;
		Object obj = null;
		UpdateResponse response = null;
		if (!inputObject.isMalformed()) {
			try {
				client = getElasticClient();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				responseObject.setErrorMessage("Exception In getting Elastic Transport Client");
				return responseObject;
			}
			IndexRequest indexRequest = new IndexRequest(inputObject.getIndex(), inputObject.getIndex(),
					inputObject.getId()).source(inputObject.getData(), XContentType.JSON);
			UpdateRequest updateRequest = new UpdateRequest(inputObject.getIndex(), inputObject.getIndex(),
					inputObject.getId()).doc(inputObject.getData(), XContentType.JSON).upsert(indexRequest);

			try {
				response = client.update(updateRequest).get();
				responseObject.setSuccessMessage(response.getResult().name());
				responseObject.setId(response.getId());
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				responseObject.setErrorMessage("Exception In updating Elastic data.");
				return responseObject;
			}
			return responseObject;
		} else {
			responseObject.setErrorMessage("Malformed input json");
		}
		return responseObject;
	}

	public static InputObject processInput(String input) {
		input = input.replaceAll("\\s", "");
		InputObject obj = null;
		JSONObject jsonObject = null;
		try {
			JSONParser parser = new JSONParser();
			jsonObject = (JSONObject) parser.parse(input);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			obj = new InputObject("", "", "", true);
			e1.printStackTrace();

		}
		/*
		 * System.out.println(input); String index=input.substring(10);
		 * index=index.substring(0, index.indexOf('"')); String
		 * id=input.substring(input.indexOf("id")+5);
		 * id=id.substring(0,id.indexOf('"')); String
		 * data=input.substring(input.indexOf("data")+6,input.lastIndexOf('}'));
		 */

		try {
			/*
			 * System.out.println(jsonObject.get("index"));
			 * System.out.println(jsonObject.get("id"));
			 * System.out.println(jsonObject.get("data"));
			 */
			obj = new InputObject(jsonObject.get("index").toString(), jsonObject.get("id").toString(),
					jsonObject.get("data").toString(), false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			obj = new InputObject("", "", "", true);
			e.printStackTrace();
		}
		;
		return obj;
	}

}