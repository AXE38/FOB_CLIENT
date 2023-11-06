package ru.axe.abs;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ru.axe.abs.models.DBDict;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Handler {

    public static String getApi_conn() {
        return api_conn;
    }

    public static void setApi_conn(String api_conn) {
        Handler.api_conn = api_conn;
    }

    private static String api_conn = "<API_LINK>";

    private static final ArrayList<DBDict> dict_all = new ArrayList<>();

    private static long role_id;

    private static String token;

    public static DB_Result getDbResult(String state) {
        Optional<DB_Result> res = db_results.stream().filter(a -> a.getState().equals(state)).findFirst();
        return res.orElse(null);
    }

    public static DBDict getDictRow(long id) {
        return dict_all.stream().filter(a -> a.getId() == id).findFirst().orElse(null);
    }

    public static List<DBDict> getDictGroup(String group_code) {
        return dict_all.stream().filter(a -> Objects.equals(a.getGroup_code(), group_code)).collect(Collectors.toList());
    }

    private static final ArrayList<DB_Result> db_results = new ArrayList();

    public static void initDicts() {
        String[] columns = {"id", "code", "group_code", "description"};
        DB_Result res = selectItems("DICT_ALL", columns, null, Optional.empty(), Optional.empty());
        if (res.getState().equals("200")) {
            NodeList data = (NodeList) res.getResult();
            DBDict dict_row;
            for (int i = 0; i < data.getLength(); i++) {
                dict_row = new DBDict();
                NodeList row = data.item(i).getChildNodes();
                for (int j = 0; j < row.getLength(); j++) {
                    switch (row.item(j).getNodeName()) {
                        case "id" -> dict_row.setId(Long.parseLong(row.item(j).getTextContent()));
                        case "code" -> dict_row.setCode(row.item(j).getTextContent());
                        case "group_code" -> dict_row.setGroup_code(row.item(j).getTextContent());
                        case "description" -> dict_row.setDescription(row.item(j).getTextContent());
                    }
                }
                dict_all.add(dict_row);
            }
            ArrayList<DBDict> db_result = (ArrayList<DBDict>) dict_all.stream().filter(a -> a.getGroup_code().equals("DB_RESULT")).toList();
            for (DBDict item : db_result) {
                db_results.add(new DB_Result(item.getDescription(), item.getCode()));
            }
        }
    }

    public static DB_Result editItems(String entity_type, Object obj) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();

            Document doc = builder.newDocument();

            Element node0 = doc.createElement("root");
            doc.appendChild(node0);
            Element node1 = doc.createElement("type");
            node1.setTextContent("UPDATE");
            node0.appendChild(node1);

            node1 = doc.createElement("entity_type");
            node1.setTextContent(entity_type);
            node0.appendChild(node1);

            node1 = doc.createElement("entity");

            Element node2;

            long id = -1;
            var t = obj.getClass();
            for (var method : t.getMethods()) {
                if (method.getName().startsWith("get") && !method.getName().equals("getClass")) {
                    String propName = method.getName().replace("get", "").toLowerCase();

                    switch (propName) {
                        case "id":
                            id = (long) method.invoke(obj, null);
                            continue;
                        case "loan_type_id":
                            propName = "loan_type";
                            break;
                        case "loan_type":
                        case "entity_type":
                        case "create_user":
                        case "create_date":
                        case "last_update_date":
                        case "last_update_user":
                            continue;
                    }
                    node2 = doc.createElement(propName);
                    if (method.getReturnType().equals(LocalDate.class)) {
                        node2.setTextContent(((LocalDate) method.invoke(obj, null)).format(MainWindow.dateFormatter));
                    } else if (method.getReturnType().equals(long.class)) {
                        node2.setTextContent(String.valueOf((long) method.invoke(obj, null)));
                    } else if (method.getReturnType().equals(float.class)) {
                        node2.setTextContent(String.valueOf((float) method.invoke(obj, null)));
                    } else {
                        node2.setTextContent((String)method.invoke(obj, null));
                    }
                    node1.appendChild(node2);
                }
            }
            node0.appendChild(node1);

            node1 = doc.createElement("where");
            Element node3;
            node2 = doc.createElement("condition");
            node3 = doc.createElement("column");
            node3.setTextContent("id");
            node2.appendChild(node3);

            node3 = doc.createElement("operator");
            node3.setTextContent("=");
            node2.appendChild(node3);

            node3 = doc.createElement("value");
            node3.setTextContent(String.valueOf(id));
            node2.appendChild(node3);
            node1.appendChild(node2);
            node0.appendChild(node1);

            node1 = doc.createElement("token");
            node1.setTextContent(token);
            node0.appendChild(node1);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StringWriter sw = new StringWriter();
            transformer.transform(source, new StreamResult(sw));
            System.out.println(sw);
            String res = httpRequest(sw.toString());
            System.out.println(res);

            InputSource is = new InputSource(new StringReader(res));
            doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            String state = doc.getElementsByTagName("state").item(0).getTextContent();
            return new DB_Result(state);

        } catch (ParserConfigurationException | IllegalAccessException | IOException | TransformerException |
                 SAXException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static DB_Result addItems(String entity_type, Object obj) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();

            Document doc = builder.newDocument();

            Element node0 = doc.createElement("root");
            doc.appendChild(node0);
            Element node1 = doc.createElement("type");
            node1.setTextContent("INSERT");
            node0.appendChild(node1);

            node1 = doc.createElement("entity_type");
            node1.setTextContent(entity_type);
            node0.appendChild(node1);

            node1 = doc.createElement("entity");

            Element node2;

            var t = obj.getClass();
            for (var method : t.getMethods()) {
                if (method.getName().startsWith("get") && !method.getName().equals("getClass")) {
                    String propName = method.getName().replace("get", "").toLowerCase();
                    switch (propName) {
                        case "loan_type_id":
                            propName = "loan_type";
                            break;
                        case "loan_type":
                        case "id":
                        case "entity_type":
                        case "create_user":
                        case "create_date":
                        case "last_update_date":
                        case "last_update_user":
                            continue;
                    }
                    node2 = doc.createElement(propName);
                    if (method.getReturnType().equals(LocalDate.class)) {
                        node2.setTextContent(((LocalDate) method.invoke(obj, null)).format(MainWindow.dateFormatter));
                    } else {
                        node2.setTextContent((String) method.invoke(obj, null));
                    }
                    node1.appendChild(node2);
                }
            }
            node0.appendChild(node1);

            node1 = doc.createElement("token");
            node1.setTextContent(token);
            node0.appendChild(node1);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StringWriter sw = new StringWriter();
            transformer.transform(source, new StreamResult(sw));
            System.out.println(sw);
            String res = httpRequest(sw.toString());
            System.out.println(res);

            InputSource is = new InputSource(new StringReader(res));
            doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            String state = doc.getElementsByTagName("state").item(0).getTextContent();
            return new DB_Result(state);

        } catch (ParserConfigurationException | IllegalAccessException | IOException | TransformerException |
                 SAXException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static DB_Result selectItems(String entity_type, String[] columns, ArrayList<DB_Where> where, Optional<Integer> offset, Optional<Integer> fetch) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();

            Document doc = builder.newDocument();

            Element node0 = doc.createElement("root");
            doc.appendChild(node0);
            Element node1 = doc.createElement("type");
            node1.setTextContent("SELECT");
            node0.appendChild(node1);

            node1 = doc.createElement("entity_type");
            node1.setTextContent(entity_type);
            node0.appendChild(node1);

            node1 = doc.createElement("entity");
            Element node2;
            for (String col: columns) {
                node2 = doc.createElement(col);
                node1.appendChild(node2);
            }
            node0.appendChild(node1);

            if (where != null) {
                node1 = doc.createElement("where");
                Element node3;
                for (DB_Where cond : where) {
                    node2 = doc.createElement("condition");
                    node3 = doc.createElement("column");
                    node3.setTextContent(cond.getColumn());
                    node2.appendChild(node3);

                    node3 = doc.createElement("operator");
                    node3.setTextContent(cond.getOperator());
                    node2.appendChild(node3);

                    node3 = doc.createElement("value");
                    node3.setTextContent(cond.getValue());
                    node2.appendChild(node3);
                    node1.appendChild(node2);
                }
                node0.appendChild(node1);
            }
            node1 = doc.createElement("token");
            node1.setTextContent(token);
            node0.appendChild(node1);
            if (offset.isPresent() && fetch.isPresent()) {
                node1 = doc.createElement("offset");
                node1.setTextContent(String.valueOf(offset.get()));
                node0.appendChild(node1);
                node1 = doc.createElement("fetch");
                node1.setTextContent(String.valueOf(fetch.get()));
                node0.appendChild(node1);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StringWriter sw = new StringWriter();
            transformer.transform(source, new StreamResult(sw));
            System.out.println(sw);
            String res = httpRequest(sw.toString());

            InputSource is = new InputSource(new StringReader(res));
            doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            String state = doc.getElementsByTagName("state").item(0).getTextContent();

            if (state.equals("200")) {
                NodeList data = doc.getElementsByTagName("data").item(0).getChildNodes();
                return new DB_Result(state, data);
            }
            return new DB_Result(state);

        } catch (ParserConfigurationException | IOException | TransformerException | SAXException e) {
            return new DB_Result("-4");
        }


    }

    private static String httpRequest(String request) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(api_conn);

        List<NameValuePair> params = new ArrayList<>(1);
        params.add(new BasicNameValuePair("request", request));

        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
    }

    public static DB_Result Auth(String login, String pass) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();

            Document doc = builder.newDocument();

            Element node0 = doc.createElement("root");

            doc.appendChild(node0);
            Element node1 = doc.createElement("type");
            node1.setTextContent("AUTH");
            node0.appendChild(node1);

            Element node2 = doc.createElement("where");
            Element node3 = doc.createElement("condition");
            Element node4 = doc.createElement("column");
            node4.setTextContent("abs_login");
            node3.appendChild(node4);
            node4 = doc.createElement("operator");
            node4.setTextContent("=");
            node3.appendChild(node4);
            node4 = doc.createElement("value");
            node4.setTextContent(login);
            node3.appendChild(node4);
            node2.appendChild(node3);

            node3 = doc.createElement("condition");
            node4 = doc.createElement("column");
            node4.setTextContent("abs_password");
            node3.appendChild(node4);
            node4 = doc.createElement("operator");
            node4.setTextContent("=");
            node3.appendChild(node4);
            node4 = doc.createElement("value");
            node4.setTextContent(pass);
            node3.appendChild(node4);
            node2.appendChild(node3);

            node0.appendChild(node2);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StringWriter sw = new StringWriter();
            transformer.transform(source, new StreamResult(sw));

            //System.out.println(sw);

            String res = httpRequest(sw.toString());

            InputSource is = new InputSource(new StringReader(res));
            doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            String state = doc.getElementsByTagName("state").item(0).getTextContent();
            if (state.equals("200")) {
                token = doc.getElementsByTagName("token").item(0).getTextContent();
                role_id = Long.parseLong(doc.getElementsByTagName("role_id").item(0).getTextContent());
                initDicts();
            }
            return new DB_Result(state);
        } catch (ParserConfigurationException | TransformerException | IOException | SAXException e) {
            return new DB_Result("-4");
        }
    }

    public static DBDict getRole() {
        return dict_all.stream().filter(a -> a.getId() == role_id).findFirst().orElse(null);
    }
}

class DB_Result {

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    private String msg;

    private String state;

    private Object result;

    public DB_Result(String state, Object result) {
        var db_result = Handler.getDbResult(state);
        if (db_result != null) {
            this.msg = db_result.msg;
        }
        this.state = state;
        this.result = result;
    }

    public DB_Result(String state) {
        this.msg = Handler.getDbResult(state).getMsg();
        this.state = state;
    }

    public DB_Result(String msg, String state, Object result) {
        this.msg = msg;
        this.state = state;
        this.result = result;
    }

    public DB_Result(String msg, String state) {
        this(msg, state, null);
    }
}

class DB_Where {
    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DB_Where(String column, String operator, String value) {
        this.column = column;
        this.operator = operator;
        this.value = value;
    }

    private String column;
    private String operator;
    private String value;
}

class ColumnAlias {
    public ColumnAlias(int index, String column_name, String column_alias, String entity_type) {
        this.index = index;
        this.column_name = column_name;
        this.column_alias = column_alias;
        this.entity_type = entity_type;
    }

    public int getIndex() {
        return index;
    }

    public String getColumn_name() {
        return column_name;
    }

    public String getColumn_alias() {
        return column_alias;
    }

    public String getEntity_type() {
        return entity_type;
    }

    private final int index;
    private final String column_name;
    private final String column_alias;
    private final String entity_type;
}