package top.soaringlab.longtailed.compilerbackend.verifier;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class StnuHDCVerifier {

    static class StnuNode {
        String id = "";
        String name = "";
        String temporal = "";
    }

    static class StnuEdge {
        String id = "";
        String name = "";
        String source = "";
        String target = "";
        String temporal = "";
    }

    static class ProcessEdge {
        String id = "";
        String name = "";
        String source = "";
        String target = "";
        /*
        Map<String, String> LowerMap = new HashMap<>(); //存储约束,key为namespace,value为具体的下界.
        Map<String, String> UpperMap = new HashMap<>(); //存储约束,key为namespace,value为具体的上界.
         */
        String Lower = "";
        String Upper = "";
        float weight = 1;
        String edgeType = ""; // ”nonFlow”为关于任意2点的requirement约束的边，"flow"为控制或消息流的边
        String constraintsType = ""; //值为"requirement"表示约束为[a,b]，或值为”contingent”表示约束为[[a,b]]
    }

    static class ProcessNode {
        String id = "";
        String name = "";
        String source = "";
        String target = "";
        /*
        Map<String, String> LowerMap = new HashMap<>(); //存储约束,key为namespace,value为具体的下界.
        Map<String, String> UpperMap = new HashMap<>(); //存储约束,key为namespace,value为具体的上界.
        */
        String Lower = "";
        String Upper = "";
        float weight = 1;
        String constraintsType = ""; //值为"requirement"表示约束为[a,b]，或值为”contingent”表示约束为[[a,b]]
        //int gateway = 0; // 默认0为非网关，1为parallel网关，2为exclusive网关
        //int gatewayOutgoingNumber = 0;
    }

    static class ParGatewayPair {
        String splitGatewayID = ""; //split网关的 ”id+end”
        //ArrayList<String> splitNodesID = new ArrayList<String>(); // split网关end事件点连接的node的start事件点集合。
        String mergeGatewayID = "";
        //ArrayList<String> mergeNodesID = new ArrayList<String>(); // node的end事件点集合：它们连接了merge 网关start事件点。
    }

    static class ExcGatewayPair {
        String splitGatewayID = "";
        String mergeGatewayID = "";
    }

    static class BordaScore {
        int score = 0;
    }

    static class GatewayTreeNode{
        String id = "";
        GatewayTreeNode parentsNode;
        ArrayList<GatewayTreeNode> childrenNodes = new ArrayList<GatewayTreeNode>();
        ArrayList<String> Threads = new ArrayList<String>();

        public GatewayTreeNode(String id, GatewayTreeNode pTNode){
           this.id = id;
           this.parentsNode = pTNode;
        }

        public GatewayTreeNode addChildrenNode(String cid) {
            GatewayTreeNode tmp = new GatewayTreeNode(cid,this);
            childrenNodes.add(tmp);
            return tmp;
        }

        public void addThread(String thread_id) {
            Threads.add(thread_id);
        }
    }

    static class ThreadTree{
        int id;
        ThreadTree parents_Thread,child_Thread;

        public ThreadTree(int id, ThreadTree pThread){
            this.id = id;
            this.parents_Thread = pThread;
        }

        public ThreadTree addChildThread(int id){
            ThreadTree tmp = new ThreadTree(id,this);
            child_Thread = tmp;
            return tmp;
        }

    }


    private Map<String, StnuNode> stnuNodeMap = new HashMap<>();

    private Map<String, StnuEdge> stnuEdgeMap = new HashMap<>();

    private Map<String, ProcessEdge> processEdgeMap = new HashMap<>();

    private Map<String, ProcessNode> processNodeMap = new HashMap<>();

    private Map<String, ParGatewayPair> parGatewayPairMap = new HashMap<>();

    private Map<String, ExcGatewayPair> excGatewayPairMap = new HashMap<>();

    private Map<String, BordaScore> bordaScore = new HashMap<>();


    private int nVertices = 0;

    private int nEdges = 0;

    private int nContingent = 0;

    private int sli_kind = 0; //0为定量约束，1为定性约束



    private ArrayList<String> pointsID = new ArrayList<String>();
    private ArrayList<String> parGatePID = new ArrayList<String>();
    private ArrayList<String> excGatePID = new ArrayList<String>();



    public boolean nonFunctionalVerify(String file, String SLIs) throws Exception {
        //得到每个SLI，如果是定性SLI则还包含了它的全序关系。
        //例如：T,P,R=[AAA>AA>A>....]
        String[] SLI_arrary = SLIs.split(",");

        //处理定性SLI,计算集合的Borda分数
        for (String sli: SLI_arrary) {
            String[] sli_inf = sli.split("=");
            if (sli_inf.length > 1){
                String[] sli_element = sli_inf[1].split(">");
                int j = 0;
                for (int i = sli_element.length-1; i >= 0; i--) {
                    BordaScore bds = new BordaScore();
                    bds.score = i;
                    bordaScore.put(sli_element[j], bds);
                    j++;
                }
            }

            readBpmnXml(file,sli);

            bordaScore.clear();
        }

        String stnuXml = writeStnuXml();
        return checkDc(stnuXml);

        /*
        readBpmnXml(file,nNamespace);
        String stnuXml = writeStnuXml();
        return checkDc(stnuXml);
        */
    }

    private void readBpmnXml(String bpmnXml, String sli) throws Exception {
        Document document = DocumentHelper.parseText(bpmnXml);
        document.getRootElement().addNamespace("constraint", "http://some-company/schema/bpmn/constraint");
        //List<Node> listEdges = document.selectNodes("//bpmn:sequenceFlow|//bpmn:messageFlow"); //若messageFlow与SLI变化有关，则统计它。
        List<Node> listEdges = document.selectNodes("//bpmn:sequenceFlow");
        List<Node> listParGateways = document.selectNodes("//bpmn:parallelGateway");
        List<Node> listExcGateways = document.selectNodes("//bpmn:exclusiveGatewa");


        ArrayList<String> edgeKeys = new ArrayList<String>();
        ArrayList<String> nodeKeys = new ArrayList<String>();
        ArrayList<String> excGatewaysKeys = new ArrayList<String>();
        ArrayList<String> parGatewaysKeys = new ArrayList<String>();
        ArrayList<String> parallelFlowsNumber = new ArrayList<String>();


         for (Node node : listEdges) {

            String source = node.valueOf("@sourceRef");
            String target = node.valueOf("@targetRef");

            String id = node.valueOf("@id");
            String name = node.valueOf("@name");
            String declarative = node.valueOf("@constraint:declarative");

            //处理关于当前sli和dom4j包中的node数据元素在bpmn的XML中的约束数据
            String constraint = node.valueOf("@constraint:temporal");
            String[] constraint_data = get_constraint_weight_in_XML(constraint,sli);
            constraint = constraint_data[0];
            float constraint_weight = Float.parseFloat(constraint_data[1]); //定量约束的weight默认返回1。

             //统计所有边的id和数量（除了功能性约束的边）
            if (declarative == "") {
                edgeKeys.add(id);
            }

            ProcessEdge processEdge = new ProcessEdge();
            processEdge.id = id;
            processEdge.name = name;

            ////得到所有边（控制流、消息流（消息流HDC时不可考虑，因为它破坏了网关的成对结构关系），prescriptive约束等）的数据
            if (constraint.indexOf("S") == 0 || constraint.indexOf("E") == 0 && declarative == "") { // 边为SLO（prescriptive约束），边上带有S或E的标识
                int t = constraint.length();
                processEdge.edgeType = "nonFlow"; //是prescriptiveEdge，非控制或消息流的边
                processEdge.constraintsType = "requirement"; // 为[a,b]

                //识别S/E[v1,v2]S/E中的 S和E
                if (constraint.indexOf("S") == 0)
                    processEdge.source = source + "+start";
                else processEdge.source = source + "+end";
                if (constraint.indexOf("S", t - 1) == t - 1)
                    processEdge.target = target + "+start";
                else processEdge.target = target + "+end";

                //这里的constraint格式为S/E[value1,value2]S/E, 实际字符串不含/，/仅在注释中表示它可以是S或E
                String[] values = constraint.split("\\[|\\]|,");
                processEdge.Lower = values[1];
                processEdge.Upper = values[2];
                processEdge.weight = constraint_weight;
                processEdgeMap.put(id, processEdge);

            } else if (constraint != "") { //边为控制流
                if (constraint.indexOf("[", 1) == 1) {
                    processEdge.edgeType = "flow"; //控制流的边
                    processEdge.source = source + "+end";
                    processEdge.target = target + "+start";

                    processEdge.constraintsType = "contingent"; //为[[a,b]]
                    String[] values = constraint.split("\\[\\[|\\]\\]|\\,");
                    processEdge.Lower = values[1];
                    processEdge.Upper = values[2];
                    processEdge.weight = constraint_weight;
                    processEdgeMap.put(id, processEdge);
                } else {
                    processEdge.edgeType = "flow"; //控制或消息流的边
                    processEdge.source = source + "+end";
                    processEdge.target = target + "+start";

                    processEdge.constraintsType = "requirement"; //为[a,b]
                    String[] values = constraint.split("\\[|\\]|,");
                    processEdge.Lower = values[1];
                    processEdge.Upper = values[2];
                    processEdge.weight = constraint_weight;
                    processEdgeMap.put(id, processEdge);
                }
            } else if (declarative == "") {
                //约束区间为[0,0]
                processEdge.edgeType = "flow"; //控制或消息流的边
                processEdge.source = source + "+end";
                processEdge.target = target + "+start";

                processEdge.constraintsType = "requirement"; //为[a,b]
                processEdge.Lower = "0";
                processEdge.Upper = "0";
                processEdge.weight = 0;
                processEdgeMap.put(id, processEdge);
            }

            //get start and end point of every element in process.
            //得到所有流程中元素（活动、网关等）的数据，该数据根据edge的数据而统计得到
            //pointsID：包含了所有结点的开始事件与结束事件，processNode为结点。例如如果结点数量为10，则pointsID的size为20。
            if (pointsID.contains(source + "+start") == false) { //边的开始结点，且该结点还未被统计，即在pointsID中不存在
                pointsID.add(source + "+start");
                pointsID.add(source + "+end");

                // 获得流程中每个结点的约束。
                String tmpXpath = "//*[@id="+"'"+source+"'"+"]";
                Node pNode = document.selectSingleNode(tmpXpath);

                //String nConstraint = pNode.valueOf("@constraint:temporal");
                String nConstraint = pNode.valueOf("@constraint:temporal");
                String[] nConstraint_data = get_constraint_weight_in_XML(nConstraint,sli);
                nConstraint = nConstraint_data[0];
                float nConstraint_weight = Float.parseFloat(nConstraint_data[1]);

                ProcessNode processNode = new ProcessNode();
                processNode.id = pNode.valueOf("@id");
                nodeKeys.add(processNode.id);
                processNode.name = pNode.valueOf("@name");
                processNode.weight = nConstraint_weight;


                // 每个结点的约束。
                if (nConstraint != "") {
                    if (nConstraint.indexOf("[", 1) == 1) {
                        processNode.constraintsType = "contingent";
                        processNode.source = source + "+start";
                        processNode.target = source + "+end";

                        String[] values = nConstraint.split("\\[\\[|\\]\\]|\\,");
                        processNode.Lower = values[1];
                        processNode.Upper = values[2];
                        processNodeMap.put(processNode.id, processNode);

                    } else {
                        processNode.constraintsType = "requirement";
                        processNode.source = source + "+start";
                        processNode.target = source + "+end";

                        String[] values = nConstraint.split("\\[|\\]|,");
                        ;
                        processNode.Lower = values[1];
                        processNode.Upper = values[2];
                        processNodeMap.put(processNode.id, processNode);
                    }
                } else {
                    //约束区间为[0,0]
                    processNode.constraintsType = "requirement";
                    processNode.source = source + "+start";
                    processNode.target = source + "+end";

                    processNode.Lower = "0";
                    processNode.Upper = "0";
                    processNodeMap.put(processNode.id, processNode);
                }
            }
            if (pointsID.contains(target + "+start") == false) { //边的结束（目标）结点,且该结点还未被统计，即在pointsID中不存在
                pointsID.add(target + "+start");
                pointsID.add(target + "+end");


                // 获得流程中每个结点的约束。
                String tmpXpath = "//*[@id="+"'"+target+"'"+"]";
                Node pNode = document.selectSingleNode(tmpXpath);

                //String nConstraint = pNode.valueOf("@constraint:temporal");
                String nConstraint = pNode.valueOf("@constraint:temporal");
                String[] nConstraint_data = get_constraint_weight_in_XML(nConstraint,sli);
                nConstraint = nConstraint_data[0];
                float nConstraint_weight = Float.parseFloat(nConstraint_data[1]);

                ProcessNode processNode = new ProcessNode();
                processNode.id = pNode.valueOf("@id");
                nodeKeys.add(processNode.id);
                processNode.name = pNode.valueOf("@name");
                processNode.weight = nConstraint_weight;

                /*
                //统计每个split并发网关的outgoing的数量
                if (pNode.valueOf("@id").matches("Gateway(.*)") == true && processNode.name.matches("(.*)s")) {
                    //第一个正则表达式（判断该结点是否为parallel网关），因为parallel网关在BPMN的XML中id值总为"Gateway....",且(第二个正则表达式)要求BPMN中的parallel网关使用 *s和*m 来人工标识网关的成对关系
                    processNode.gateway = 1;
                    String gXpath = "//bpmn:parallelGateway[@id="+"'"+processNode.id+"']"+"/bpmn:outgoing";
                    List<Node> listOutgoing = document.selectNodes(gXpath);
                    for (Node outgoingNode : listOutgoing) {
                        processNode.gatewayOutgoingNumber++;
                    }
                    parallelFlowsNumber.add(String.valueOf(processNode.gatewayOutgoingNumber));
                } else if (pNode.valueOf("@id").matches("ExclusiveGateway(.*)") == true)
                    processNode.gateway = 2;

                 */


                // 处理每个结点的约束。
                if (nConstraint != "") {
                    if (nConstraint.indexOf("[", 1) == 1) {
                        processNode.constraintsType = "contingent";
                        processNode.source = target + "+start";
                        processNode.target = target + "+end";

                        String[] values = nConstraint.split("\\[\\[|\\]\\]|\\,");
                        processNode.Lower = values[1];
                        processNode.Upper = values[2];
                        processNodeMap.put(processNode.id, processNode);

                    } else {
                        processNode.constraintsType = "requirement";
                        processNode.source = target + "+start";
                        processNode.target = target + "+end";

                        String[] values = nConstraint.split("\\[|\\]|,");
                        processNode.Lower = values[1];
                        processNode.Upper = values[2];
                        processNodeMap.put(processNode.id, processNode);
                    }
                } else {
                    //约束区间为[0,0]
                    processNode.constraintsType = "requirement";
                    processNode.source = target + "+start";
                    processNode.target = target + "+end";

                    processNode.Lower = "0";
                    processNode.Upper = "0";
                    processNodeMap.put(processNode.id, processNode);
                }
            }
        }

        //构建流程与约束的有向图
        int pointEventNumber = pointsID.size(); //流程中所有结点的开始与结束事件数量，即全局有向图的顶点集合


        String flowAdjacentMatrix[][] = new String[pointEventNumber][pointEventNumber]; // 该矩阵用于建模控制约束
        String nonFlowAdjacentMatrix[][] = new String[pointEventNumber][pointEventNumber]; //该矩阵用于（非建模控制流）prescriptive约束（即2个非连续的point的约束）
        int constraintyOritationMatrix[][] = new int[pointEventNumber][pointEventNumber]; //该矩阵用于建模 约束的原有方向（STNU映射时用），以区分上述2个矩阵中的i至j,和j至i哪个是Upper（为1）, 哪个是Lower（为-1）://该矩阵由于建模其他矩阵中约束的方向，1为正，-1为反方向。
        String constraintTypeMatrix[][] = new String[pointEventNumber][pointEventNumber]; //该矩阵用于最终映射STNU时判断 2个point的约束类型（即为requirement:[]或contingent[[]]）
        String hdc_relationship[][] = new String[pointEventNumber][pointEventNumber]; //该矩阵用于记录2个point之间的约束（如果存在）所属于的HDC层次（即所属的split 并行网关，或最顶层top）
        int hdc_thread_relationship[][] = new int[pointEventNumber][pointEventNumber]; //该矩阵用于记录2个point之间的约束所属于的thread(需与hdc_relationship结合使用).

        //创建矩阵，存储XML中的SLA数据

        for (int i = 0; i < edgeKeys.size(); i++) {
            ProcessEdge tmp_processEdge = processEdgeMap.get(edgeKeys.get(i));
            if (tmp_processEdge.edgeType == "flow"){
                flowAdjacentMatrix[pointsID.indexOf(tmp_processEdge.source)][pointsID.indexOf(tmp_processEdge.target)] = tmp_processEdge.Upper;
                flowAdjacentMatrix[pointsID.indexOf(tmp_processEdge.target)][pointsID.indexOf(tmp_processEdge.source)] = tmp_processEdge.Lower;
            } else {
                nonFlowAdjacentMatrix[pointsID.indexOf(tmp_processEdge.source)][pointsID.indexOf(tmp_processEdge.target)] = tmp_processEdge.Upper;
                nonFlowAdjacentMatrix[pointsID.indexOf(tmp_processEdge.target)][pointsID.indexOf(tmp_processEdge.source)] = tmp_processEdge.Lower;
            }
            if (tmp_processEdge.constraintsType == "requirement") {
                constraintTypeMatrix[pointsID.indexOf(tmp_processEdge.source)][pointsID.indexOf(tmp_processEdge.target)] = "requirement";
                constraintTypeMatrix[pointsID.indexOf(tmp_processEdge.target)][pointsID.indexOf(tmp_processEdge.source)] = "requirement";
            } else {
                constraintTypeMatrix[pointsID.indexOf(tmp_processEdge.source)][pointsID.indexOf(tmp_processEdge.target)] = "contingent";
                constraintTypeMatrix[pointsID.indexOf(tmp_processEdge.target)][pointsID.indexOf(tmp_processEdge.source)] = "contingent";
            }
            //该矩阵由于建模其他矩阵中约束的方向，1为正，-1为反方向。
            constraintyOritationMatrix[pointsID.indexOf(tmp_processEdge.source)][pointsID.indexOf(tmp_processEdge.target)] = 1;
            constraintyOritationMatrix[pointsID.indexOf(tmp_processEdge.target)][pointsID.indexOf(tmp_processEdge.source)] = -1;
        }
        for (int i = 0; i < nodeKeys.size(); i++) {
            ProcessNode tmp_processNode = processNodeMap.get(nodeKeys.get(i));
            flowAdjacentMatrix[pointsID.indexOf(tmp_processNode.source)][pointsID.indexOf(tmp_processNode.target)] = tmp_processNode.Upper;
            flowAdjacentMatrix[pointsID.indexOf(tmp_processNode.target)][pointsID.indexOf(tmp_processNode.source)] = tmp_processNode.Lower;

            if (tmp_processNode.constraintsType == "requirement") {
                constraintTypeMatrix[pointsID.indexOf(tmp_processNode.source)][pointsID.indexOf(tmp_processNode.target)] = "requirement";
                constraintTypeMatrix[pointsID.indexOf(tmp_processNode.target)][pointsID.indexOf(tmp_processNode.source)] = "requirement";
            } else {
                constraintTypeMatrix[pointsID.indexOf(tmp_processNode.source)][pointsID.indexOf(tmp_processNode.target)] = "contingent";
                constraintTypeMatrix[pointsID.indexOf(tmp_processNode.target)][pointsID.indexOf(tmp_processNode.source)] = "contingent";
            }
            //该矩阵由于建模其他矩阵中约束的方向，1为正，-1为反方向。
            constraintyOritationMatrix[pointsID.indexOf(tmp_processNode.source)][pointsID.indexOf(tmp_processNode.target)] = 1;
            constraintyOritationMatrix[pointsID.indexOf(tmp_processNode.target)][pointsID.indexOf(tmp_processNode.source)] = -1;
        }

        //统计网关的关系对与层次结构
        for (Node gateway : listParGateways) {
            String tmp_id = gateway.valueOf("@id");
            parGatewaysKeys.add(tmp_id);
            parGatePID.add(tmp_id+"+start");
            parGatePID.add(tmp_id+"+end");
        }

        for (Node gateway : listExcGateways) {

            String tmp_id = gateway.valueOf("@id");
            excGatewaysKeys.add(tmp_id);
            excGatePID.add(tmp_id+"+start");
            excGatePID.add(tmp_id+"+end");
        }

        Node startNode = document.selectNodes("//bpmn:startEvent").get(0); //限定BPMN中只有一个”开始结点”，该语句才正确
        String startPointIdStartNode = startNode.valueOf("@id")+"+start";

        GatewayTreeNode rootGTNode = new GatewayTreeNode("top",null);
        ThreadTree rootThread = new ThreadTree(0,null);

        //调用递归函数，获取各元素所属的HDC层次
        compute_paraGate_relationship(startPointIdStartNode,rootGTNode,rootThread,hdc_relationship,flowAdjacentMatrix,constraintyOritationMatrix,hdc_thread_relationship,pointEventNumber);

        //至此，验证HDC验证的主函数还未写完...........................

        /*
        //coding时的临时debug,看compute_paraGate_relationship逻辑是否正确。
        //测试hdc_relationship 是否正确，需要人工看t和t2中的值。
        String s1 = "";
        String s2 = "";
        String s3 = "";
        parGatewayPairMap.values();

        for (int i=0; i<hdc_relationship.length; i++)
            for(int j=0; j<hdc_relationship[i].length; j++) {
                if (hdc_relationship[i][j] == "top") {
                    s1 = s1 + pointsID.get(i) + " -> " + pointsID.get(j) + " on Thread " + String.valueOf(hdc_thread_relationship[i][j]) + " || ";
                }
                else if (hdc_relationship[i][j] == rootGTNode.childrenNodes.get(0).id){
                    s2 = s2 + pointsID.get(i) + " -> " + pointsID.get(j) + " on Thread " + String.valueOf(hdc_thread_relationship[i][j]) + " || ";
                }
                else if (hdc_relationship[i][j] == rootGTNode.childrenNodes.get(0).childrenNodes.get(0).id) {
                    s3 = s3 + pointsID.get(i) + " -> " + pointsID.get(j) + " on Thread " + String.valueOf(hdc_thread_relationship[i][j]) + " || ";
                }
            }

        String[] t = {s1,s2,s3};
        int[] t2 = {s1.split("\\|\\|").length-1,s2.split("\\|\\|").length-1,s3.split("\\|\\|").length-1};
        */

    }

    private String[] get_constraint_weight_in_XML(String constraint,String sli){
        String constraint_weight = "1";
        if (sli.contains(">") == false){
            //处理不同定量 SLI的约束
            //constraint格式为格式为：namespace:[value1,value2] or namespace:S/E[value1,value2]S/E or *:[[*,*]] , 且约束字符串包含多个 "约束1，约束2，.."
            //定性的约束如果非0，则可以为...[value1,value2,w]... 或 默认权重为1则为[value1,value2]; 定量权重默认为1，[0,0]则权重默认为0
            //使用&分隔约束中的SLI
            constraint = constraint.replace("S,","S&");
            constraint = constraint.replace("E,","E&");
            constraint = constraint.replace("],","]&");
            String[] constraints = constraint.split("&");

            for(String c : constraints){
                if (c.contains(sli+":") == true)
                {
                    constraint = c.split(":")[1];
                    break;
                }
                else {
                    //边上没有约束（后续的外部函数，只会统计控制流），则约束为””，且权重为0
                    constraint = "";
                    constraint_weight = "0";
                }
            }
            String[] data = {constraint,constraint_weight};
            return data;
        }
        else {
            sli_kind = 1;
            //处理定性SLI,计算集合的Borda分数
            String[] sli_inf = sli.split("=");
            String sli_name = sli_inf[0];

            //constraint格式为：namespace:[value1,value2,weight],... or namespace:S/E[value1,value2,weight]S/E,... or [[,]]
            //使用&分隔约束中的SLI
            constraint = constraint.replace("S,","S&");
            constraint = constraint.replace("E,","E&");
            constraint = constraint.replace("],","]&");
            String[] constraints = constraint.split("&");

            for(String c : constraints){
                if (c.contains(sli_name+":") == true)
                {
                    String tem_constraint = c.split(":")[1];

                    //去掉原始约束中的weight部分，例如S[v1,v2,w]E 变成 S[v1,v2]E
                    String[] tem_constraints = tem_constraint.split(",");

                    if (tem_constraints.length > 2) {
                        int tem_index = tem_constraints[2].indexOf("]"); //找到"w]e"中]的位置，用于去掉约束中的w部分
                        char[] tem_char = tem_constraints[2].toCharArray();
                        constraint = tem_constraints[0]+","+tem_constraints[1]; //此时约束可为："S[V1,V2" 或者 "S[[v1,v2"
                        for (int i=tem_index; i<tem_char.length; i++){
                            constraint = constraint + tem_char[i];
                        }
                        //因为权重的区间只会是数字（0至无穷），因此例如即使S[v1,v2,w]E中的v1或v2含有S或E作为关键字，也可replace，因为这里我们只需要w中的数据。
                        constraint_weight = tem_constraint.replace("[","").replace("]","").replace("S","").replace("E","").split(",")[2];
                        break;
                    }
                    else {
                        //如果定性约束位置权重（例如S[v1,v2]E ），则默认置为 1
                        constraint = c;
                        constraint_weight = "1";
                    }
                }
                else {
                    //边上没有约束（后续只会统计控制流），则约束为””，且权重为0，
                    constraint = "";
                    constraint_weight = "0";
                }
            }

            String[] data = {constraint,constraint_weight};
            return data;
        }

    }

    //数组是引用类型，因此是实参，修改函数中的引用类型变量，上级函数中的变量也会更新。
    //该函数用于计算在验证HDC是层次化关系，流程由并行网关而分层
    private void compute_paraGate_relationship(String begin_point_ID, GatewayTreeNode current_GTNode, ThreadTree current_thread,String[][] hdc_relationship, String[][] flowAdjacentMatrix, int[][] constraintyOritationMatrix, int[][] hdc_thread_relationship,int matrix_column_number){

        String next_point_ID = "";
        //begin_point_ID在flow矩阵中的行为起点i，遍历所用与该行相连的列。
        int i = pointsID.indexOf(begin_point_ID);
        for (int j = 0; j < matrix_column_number; j++){
            //如果i至j之间存在控制流。constraintyOritationMatrix[i][j] 为1是正方向（与控制流方向一致）
            if (constraintyOritationMatrix[i][j] == 1){
                next_point_ID = pointsID.get(j);
                int temp_j_next_point_end_id = pointsID.indexOf(next_point_ID.replace("+start","").replace("+end","")+"+end");
                //如果next_poinf_ID(j)为并行网关的end point,且该并行网关是split并行网关
                //如果flow矩阵中的j行的outgoing的flows大于1，则is_split_gateway返回true；小于等于1（实际不存在小于1）则返回false.
                if (parGatePID.contains(next_point_ID) && next_point_ID.matches("(.*)end") && is_split_gateway(constraintyOritationMatrix, temp_j_next_point_end_id, matrix_column_number) > 1){
                    int thread_number = is_split_gateway(constraintyOritationMatrix, temp_j_next_point_end_id, matrix_column_number);
                    if (thread_number > 1){
                        String next_cnode_key = next_point_ID.replace("+end","");
                        //进入更深一层的HDC关系
                        GatewayTreeNode cGTNode = current_GTNode.addChildrenNode(next_cnode_key);
                        hdc_relationship[i][j] = current_GTNode.id;
                        ParGatewayPair parGatewayPair = new ParGatewayPair();
                        parGatewayPair.splitGatewayID = next_point_ID;
                        parGatewayPairMap.put(next_cnode_key,parGatewayPair); //key不含next point id中的”+end”

                        hdc_thread_relationship[i][j] =  current_thread.id;
                        ThreadTree child_thread = current_thread.addChildThread(0);

                        for (int k = 1; k <= thread_number; k++){
                            cGTNode.Threads.add(String.valueOf(k));
                        }
                        compute_paraGate_relationship(next_point_ID,cGTNode,child_thread,hdc_relationship,flowAdjacentMatrix,constraintyOritationMatrix,hdc_thread_relationship,matrix_column_number);
                    }
                }
                //如果next_poinf_ID(j)为并行网关的start point,且该并行网关是merge并行网关
                else if (parGatePID.contains(next_point_ID) && next_point_ID.matches("(.*)start") && is_split_gateway(constraintyOritationMatrix, temp_j_next_point_end_id, matrix_column_number) <= 1){
                    //回溯到上一层的HDC关系
                    GatewayTreeNode pGTnode = current_GTNode.parentsNode;
                    hdc_relationship[i][j] = current_GTNode.id;
                    ParGatewayPair parGatewayPair = parGatewayPairMap.get(current_GTNode.id);  //key不含next point id中的”+end”
                    parGatewayPair.mergeGatewayID = next_point_ID;

                    hdc_thread_relationship[i][j] = current_thread.id;
                    compute_paraGate_relationship(next_point_ID,pGTnode,current_thread.parents_Thread,hdc_relationship,flowAdjacentMatrix,constraintyOritationMatrix,hdc_thread_relationship,matrix_column_number);
                }
                else {
                    //这个if用于给同一并行网关中不同flow赋予不同的thread编号。
                    //如果当前begin point 是并行网关，且begin point 是并发网关的end Point，同时该网关是split：则存在2个以上的thread，且赋予编号。
                    if (parGatePID.contains(begin_point_ID) && begin_point_ID.matches("(.*)end") && is_split_gateway(constraintyOritationMatrix, i, matrix_column_number) > 1){
                        hdc_relationship[i][j] = current_GTNode.id;
                        //设置（split网关所分化出的flow）当前flow的thread编号
                        current_thread.id++;
                        hdc_thread_relationship[i][j] = current_thread.id;
                        compute_paraGate_relationship(next_point_ID,current_GTNode,current_thread,hdc_relationship,flowAdjacentMatrix,constraintyOritationMatrix,hdc_thread_relationship,matrix_column_number);

                    }
                    else {
                        hdc_relationship[i][j] = current_GTNode.id;
                        hdc_thread_relationship[i][j] = current_thread.id;
                        compute_paraGate_relationship(next_point_ID,current_GTNode,current_thread,hdc_relationship,flowAdjacentMatrix,constraintyOritationMatrix,hdc_thread_relationship,matrix_column_number);
                    }

                }

            }
        }

    }

    //返回row_index作为起点，split出的flow数量。
    private int is_split_gateway(int constraintyOritationMatrix[][],int row_index, int matrix_column_number){
        int flow_outgoing_number = 0;
        for (int j = 0; j < matrix_column_number; j++){
            if (constraintyOritationMatrix[row_index][j] == 1){
                flow_outgoing_number++;
            }
        }
        return flow_outgoing_number;
    }



// below need to update.

/*
    private void readBpmnXml(String bpmnXml) throws Exception {
        Document document = DocumentHelper.parseText(bpmnXml);
        document.getRootElement().addNamespace("constraint", "http://some-company/schema/bpmn/constraint");
        List<Node> list = document.selectNodes("//*[@constraint:temporal]");
        for (Node node : list) {
            String localName = node.valueOf("local-name()");
            String id = node.valueOf("@id");
            String name = node.valueOf("@name");
            String temporal = node.valueOf("@constraint:temporal");
            if (localName.equals("sequenceFlow") || localName.equals("messageFlow")) {
                String source = node.valueOf("@sourceRef");
                String target = node.valueOf("@targetRef");
                StnuEdge stnuEdge = new StnuEdge();
                stnuEdge.id = id;
                stnuEdge.name = name;
                stnuEdge.source = source;
                stnuEdge.target = target;
                stnuEdge.temporal = temporal;
                stnuEdgeMap.put(id, stnuEdge);
            } else {
                StnuNode stnuNode = new StnuNode();
                stnuNode.id = id;
                stnuNode.name = name;
                stnuNode.temporal = temporal;
                stnuNodeMap.put(id, stnuNode);
            }
        }
    }
*/
    private String writeStnuXml() throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new ClassPathResource("stnu.xml").getFile());

        Element graphElement = (Element) document.selectSingleNode("graphml/graph");

        for (StnuNode stnuNode : stnuNodeMap.values()) {
            graphElement.addElement("node").addAttribute("id", stnuNode.id + "_start");
            graphElement.addElement("node").addAttribute("id", stnuNode.id + "_end");
            nVertices = nVertices + 2;

            addEdgeElement(graphElement, stnuNode.id, stnuNode.id + "_start", stnuNode.id + "_end", stnuNode.temporal);
        }

        for (StnuEdge stnuEdge : stnuEdgeMap.values()) {
            if (stnuEdge.temporal.equals("sequence") || stnuEdge.temporal.startsWith("[[")) {
                addEdgeElement(graphElement, stnuEdge.id, stnuEdge.source + "_end", stnuEdge.target + "_start", stnuEdge.temporal);
            } else {
                addEdgeElement(graphElement, stnuEdge.id, stnuEdge.source + "_start", stnuEdge.target + "_end", stnuEdge.temporal);
            }
        }

        Element nVerticesElement = (Element) graphElement.selectSingleNode("data[@key='nVertices']");
        nVerticesElement.setText(String.valueOf(nVertices));
        Element nEdgesElement = (Element) graphElement.selectSingleNode("data[@key='nEdges']");
        nEdgesElement.setText(String.valueOf(nEdges));
        Element nContingentElement = (Element) graphElement.selectSingleNode("data[@key='nContingent']");
        nContingentElement.setText(String.valueOf(nContingent));

        return document.asXML();
    }

    private void addEdgeElement(Element graphElement, String id, String source, String target, String temporal) {
        Element lowerEdgeElement = graphElement.addElement("edge")
                .addAttribute("id", id + "_lower")
                .addAttribute("source", target)
                .addAttribute("target", source);
        Element upperEdgeElement = graphElement.addElement("edge")
                .addAttribute("id", id + "_upper")
                .addAttribute("source", source)
                .addAttribute("target", target);
        nEdges = nEdges + 2;

        if (temporal.equals("sequence")) {
            addDataElement(lowerEdgeElement, "requirement", "-0");
            addDataElement(upperEdgeElement, "requirement", "0");
        } else if (temporal.startsWith("[[")) {
            String[] values = temporal.split("\\[\\[|\\]\\]|,");
            addDataElement(lowerEdgeElement, "contingent", "-" + values[1]);
            addDataElement(upperEdgeElement, "contingent", values[2]);
            nContingent = nContingent + 1;
        } else {
            String[] values = temporal.split("\\[|\\]|,");
            addDataElement(lowerEdgeElement, "requirement", "-" + values[1]);
            addDataElement(upperEdgeElement, "requirement", values[2]);
        }
    }

    private void addDataElement(Element edgeElement, String type, String value) {
        edgeElement.addElement("data")
                .addAttribute("key", "Type")
                .addText(type);
        edgeElement.addElement("data")
                .addAttribute("key", "Value")
                .addText(value);
    }

    private boolean checkDc(String stnuXml) throws Exception {
        String directoryName = "stnu";
        File directoryFile = new File(directoryName);
        if (!directoryFile.exists()) {
            directoryFile.mkdirs();
        }

        String toolName = "CSTNU-Tool-4.5.jar";
        File toolFile = new File(directoryFile, toolName);
        if (!toolFile.exists()) {
            InputStream inputStream = new ClassPathResource(toolName).getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(toolFile);
            int len;
            byte[] b = new byte[1024];
            while ((len = inputStream.read(b)) != -1) {
                fileOutputStream.write(b, 0, len);
            }
            inputStream.close();
            fileOutputStream.close();
        }

        String stnuName = UUID.randomUUID() + ".stnu";
        File stnuFile = new File(directoryFile, stnuName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(stnuFile));
        bufferedWriter.write(stnuXml);
        bufferedWriter.close();

        String[] runCommand = new String[]{"java", "-cp", toolName, "it.univr.di.cstnu.algorithms.STNU", stnuName};
        Process process = Runtime.getRuntime().exec(runCommand, null, directoryFile);
        if (!process.waitFor(10, TimeUnit.SECONDS)) {
            process.destroy();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        bufferedReader.close();
        String output = stringBuilder.toString();
        return output.contains("The given STNU is dynamic controllable!");
    }
}


// below is draft code

/*

        //获取并发网关的关系对，即每一对*s和*m，且满足*等于*
        for (int i = 0 ; i < parGatewaysKeys.size(); i++) {
            ProcessNode pNode1 = processNodeMap.get(parGatewaysKeys.get(i));
            for (int j = i+1; j < parGatewaysKeys.size(); j++) {
                ProcessNode pNode2 = processNodeMap.get(parGatewaysKeys.get(j));
                if (pNode1.name.charAt(0) == pNode2.name.charAt(0)) {
                    ParGatewayPair parGatewayPair = new ParGatewayPair();
                    if (pNode1.name.charAt(1) == 's' && pNode2.name.charAt(1) == 'm') {
                        parGatewayPair.splitGatewayID = pNode1.id + "+end";
                        //获取Split网关outgoing相连接的node的id
                        String gXpath = "//bpmn:parallelGateway[@id="+"'"+pNode1.id+"']"+"/bpmn:outgoing";
                        List<Node> listOutgoing = document.selectNodes(gXpath);
                        for (Node outgoingNode : listOutgoing) {
                            String outgoingNodeID = processEdgeMap.get(outgoingNode.getText()).target;
                            parGatewayPair.splitNodesID.add(outgoingNodeID);
                        }

                        parGatewayPair.mergeGatewayID = pNode2.id + "+start";
                        //获取merge网关incoming相连接的node的id
                        gXpath = "//bpmn:parallelGateway[@id="+"'"+pNode2.id+"']"+"/bpmn:incoming";
                        List<Node> listIncoming = document.selectNodes(gXpath);
                        for (Node incomingNode : listIncoming) {
                            String incomingNodeID = processEdgeMap.get(incomingNode.getText()).source;
                            parGatewayPair.mergeNodesID.add(incomingNodeID);
                        }
                        parGatewayPairMap.put(pNode1.id + "+end",parGatewayPair);

                    } else if (pNode2.name.charAt(1) == 's' && pNode1.name.charAt(1) == 'm') {
                        parGatewayPair.splitGatewayID = pNode2.id + "+end";
                        //获取Split网关outgoing相连接的node的id
                        String gXpath = "//bpmn:parallelGateway[@id="+"'"+pNode2.id+"']"+"/bpmn:outgoing";
                        List<Node> listOutgoing = document.selectNodes(gXpath);
                        for (Node outgoingNode : listOutgoing) {
                            String outgoingNodeID = processEdgeMap.get(outgoingNode.getText()).target;
                            parGatewayPair.splitNodesID.add(outgoingNodeID);
                        }

                        parGatewayPair.mergeGatewayID = pNode1.id + "+start";
                        //获取merge网关incoming相连接的node的id
                        gXpath = "//bpmn:parallelGateway[@id="+"'"+pNode1.id+"']"+"/bpmn:incoming";
                        List<Node> listIncoming = document.selectNodes(gXpath);
                        for (Node incomingNode : listIncoming) {
                            String incomingNodeID = processEdgeMap.get(incomingNode.getText()).source;
                            parGatewayPair.mergeNodesID.add(incomingNodeID);
                        }
                        parGatewayPairMap.put(pNode2.id + "+end",parGatewayPair);
                    }
                }
            }
        }

        //获取排它网关的关系对，即每一对*s和*m，且满足*等于*
        for (int i = 0 ; i < excGatewaysKeys.size(); i++) {
            ProcessNode pNode1 = processNodeMap.get(excGatewaysKeys.get(i));
            for (int j = i+1; j < excGatewaysKeys.size(); j++) {
                ProcessNode pNode2 = processNodeMap.get(excGatewaysKeys.get(j));
                if (pNode1.name.charAt(0) == pNode2.name.charAt(0)) {
                    ExcGatewayPair excGatewayPair = new ExcGatewayPair();
                    if (pNode1.name.charAt(1) == 's' && pNode2.name.charAt(1) == 'm') {
                        excGatewayPair.splitGatewayID = pNode1.id + "+end";
                        excGatewayPair.mergeGatewayID = pNode2.id + "+start";
                        excGatewayPairMap.put(pNode1.id + "+end",excGatewayPair);
                    } else if (pNode2.name.charAt(1) == 's' && pNode1.name.charAt(1) == 'm') {
                        excGatewayPair.splitGatewayID = pNode2.id + "+end";
                        excGatewayPair.mergeGatewayID = pNode1.id + "+start";
                        excGatewayPairMap.put(pNode2.id + "+end",excGatewayPair);
                    }
                }
            }
        }

        */
