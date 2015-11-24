// Copyright (c) Corporation for National Research Initiatives
package org.python.core;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.lang.reflect.Array;

import org.python.expose.ExposedMethod;
import org.python.expose.ExposedNew;
import org.python.expose.ExposedType;
import org.python.expose.MethodType;

import java.sql.*;

import java.util.concurrent.ConcurrentMap;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.pool.OracleDataSource;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;

import batch.EvalService;
import batch.tcp.TCPClient;
import batch.util.BatchTransport;
import batch.json.JSONTransport;

import org.python.util.JenaTutorialExamples;

import org.python.ReL.PyRelConnection;
import org.python.ReL.SIMHelper;
import org.python.ReL.SQLVisitor;
import org.python.ReL.ProcessLanguages;

// import org.apache.commons.lang3.math;

/**
 * A builtin python tuple.
 */
@ExposedType(name = "tuple", base = PyObject.class, doc = BuiltinDocs.tuple_doc)
public class PyTuple extends PySequenceList implements List {

	public static final PyType TYPE = PyType.fromClass(PyTuple.class);
	// If we use a class type, mark down that we may have a select that is expected to return an instance of the type
	private ArrayList<PyType> relQueryInstancesType = new ArrayList<PyType>();
	private Collection<String> relQueryInstancesTypeNames = new ArrayList<String>();

	private static SQLVisitor v;
	private Boolean flag = false;
	// private final PyObject[] array;
	private PyObject[] array;           // Changed for ReL

	private volatile List<PyObject> cachedList = null;

	private static final PyTuple EMPTY_TUPLE = new PyTuple();

	public PyTuple() {
		this(TYPE, Py.EmptyObjects);
	}

	public PyTuple(PyObject... elements) {
		this(TYPE, elements);
	}

	public PyTuple(PyType subtype, PyObject[] elements) {
		super(subtype);
		if (elements == null) {
			array = new PyObject[0];
		} else {
			array = new PyObject[elements.length];
			System.arraycopy(elements, 0, array, 0, elements.length);
		}
	}

	public PyTuple(PyObject[] elements, boolean copy) {
		this(TYPE, elements, copy);
	}

	public PyTuple(PyType subtype, PyObject[] elements, boolean copy) {
		super(subtype);

		if (copy) {
			array = new PyObject[elements.length];
			System.arraycopy(elements, 0, array, 0, elements.length);
		} else {
			array = elements;
		}
	}

	// This constructor was added for ReL
	public PyTuple(PyObject[] elements, String ReLstring, String ReLmode, PyObject connection) {
		this(TYPE, elements, ReLstring, ReLmode, connection);
	}
	// This constructor was added for ReL
	public PyTuple(PyType subtype, PyObject[] elements, String ReLstring, String ReLmode, PyObject connection) {
		super(subtype);
		PyRelConnection conn = (PyRelConnection)connection;
		String[] strings = ReLstring.split(";");
		int size = Math.max(strings.length - 1, elements.length);
		String ReLstmt = strings[0];
		for (int i = 0; i < size; i++) {
			if (i + 1 < strings.length) {
				if(strings[i + 1].charAt(0) == ' ')
					ReLstmt += strings[i + 1].substring(1); // substring gets rid of an extraneous space at the beginning of the string
				else
					ReLstmt += strings[i + 1];
			}
			if (i < elements.length) {
				if (elements[i] instanceof PyType) {
					// Maintain information about the PyTypes since this means we will need to return
					// instances of these types.
					relQueryInstancesType.add((PyType)elements[i]);
					relQueryInstancesTypeNames.add(((PyType)elements[i]).getName());
					//ReLstmt += " " + ((PyType)elements[i]).getName() + " ";
					ReLstmt += ((PyType)elements[i]).getName();
				}
				else if (elements[i].getType().pyGetName().toString() == "str") {
					if(elements[i].toString().trim().charAt(0) == '(' ||
							elements[i].toString().trim().charAt(0) == '[' ||
							elements[i].toString().trim().charAt(0) == '{' ||
							//elements[i].toString().trim().charAt(0) == '"') ReLstmt += " " + elements[i].toString() + " ";
							elements[i].toString().trim().charAt(0) == '"') ReLstmt += elements[i].toString();
						// else ReLstmt += " \'" + elements[i].toString() + "' ";
					else ReLstmt += elements[i].toString();
				}
				else
					//ReLstmt += " " + elements[i].toString() + " ";
					ReLstmt += elements[i].toString();
			}
		}

		ReLstmt = ReLstmt.trim();
		if (conn.getDebug() == "debug") System.out.println("ReLstmt is: " + ReLstmt);

		if(ReLmode == "Neo4j") {
			ProcessLanguages processLanguage = new ProcessLanguages(conn);
			String sim = processLanguage.processNeo4j(ReLstmt);
			ReLmode = "SIM";
			ReLstmt = sim;
		}

		if(ReLmode == "SQL") {
			if (conn.getConnectionType() == "native_mode") {
				/* Test in R with
				On localhost run: pcannata$ dist/bin/jython RestfulReL/mike68561-restful-rel-ea5e3cef544e/restful_start.py
				In R run:
				df <- data.frame(eval(parse(text=substring(getURL(URLencode('localhost:5001/rest/native/?query="select empno, ename, job, mgr, hiredate, sal, nvl(comm,0) comm, deptno from emp"'), httpheader=c(DB='jdbc:oracle:thin:@129.152.144.84:1521/PDB1.usuniversi01134.oraclecloud.internal', USER='DV_Vehicles', PASS='orcl', MODE='native_mode', MODEL='model', returnFor = 'R', returnDimensions = 'False'), verbose = TRUE), 1, 2^31-1)))); df
				*/
				if (ReLstmt.charAt(0) == '\'' ) ReLstmt = ReLstmt.substring(1, ReLstmt.length()-1); //This happens for a py program like:
				//sql = "select * from emp"
				//print SQL "" sql
				if (conn.getDebug() == "debug") System.out.println("Remote: " + ReLstmt);
				runAndOutputTuples(conn, ReLstmt);
			} else if (conn.getConnectionType() == "rdf_mode" || conn.getConnectionType() == "ag_sql_rdf_mode"|| conn.getConnectionType() == "ag_sparql_rdf_mode") {

				// If some type of rdf_mode, transfor SQL to SPARQL as follows:
				// In PyTuple.doRDF: if (statement instanceof Select), call SQLVisitor.getSelect
				//    In SQLVisitor.getSelect, somehow gets to visit, which calls visitSelect_buildSPARQL
				//        visitSelect_buildSPARQL returns the SPARQL statement to SQLVisitor.getSelect
				//    SQLVisitor.getSelect returns the SPARQL to PyTuple
				// PyTuple.doRDF then calls runAndOutputTuples, which calls conn.executeQuery(ReLstmt) and the results from this are processed and stored in the results field of PyTuple

				if (conn.getConnectionType() == "rdf_mode" || conn.getConnectionType() == "ag_sql_rdf_mode") {
					CCJSqlParserManager pm = new CCJSqlParserManager();
					net.sf.jsqlparser.statement.Statement statement = null;
					try {
						statement = (net.sf.jsqlparser.statement.Statement)pm.parse(new StringReader(ReLstmt));
					} catch (Exception e) {
						System.out.println(e);
					}
					if (conn.getDebug() == "debug") System.out.println("jsqlstmt is: " + statement.toString());
					if (conn.getConnectionType() == "rdf_mode") {
				   /* Test in R with
				   On localhost run: pcannata$ dist/bin/jython RestfulReL/mike68561-restful-rel-ea5e3cef544e/restful_start.py
				   In R run:
				   df <- data.frame(eval(parse(text=substring(getURL(URLencode('localhost:5001/rest/native/?query="select * from emp"'), httpheader=c(DB='jdbc:oracle:thin:@128.83.138.158:1521:orcl', USER='C##cs347_prof', PASS='orcl_prof', MODE='rdf_mode', MODEL='F2014', returnFor = 'R', returnDimensions = 'False'), verbose = TRUE), 1, 2^31-1)))); df
				   */
						doRDF(statement, conn);
					} else if (conn.getConnectionType() == "ag_sql_rdf_mode") {
				   /*
			   
				   r <- data.frame(fromJSON(getURL(URLencode('ec2-user@ec2-52-10-0-189.us-west-2.compute.amazonaws.com:5001/rest/native/?query="SELECT * from Person"'),httpheader=c(DB='jdbc:oracle:thin:@129.152.144.84:1521/PDB1.usuniversi01134.oraclecloud.internal',USER='DV_Diamonds',PASS='orcl',MODE='ag_sql_rdf_mode',MODEL='model',returnDimensions = 'False',returnFor = 'JSON', q="WHERE"),verbose = TRUE))); r
			   
				   SELECT v1 "HIREDATE", v2 "COMM", v3 "DBUNIQUEID", v4 "JOB", v5 "DEPTNO", v6 "SAL", v7 "ENAME", v8 "MGR", v9 "EMPNO"
				   FROM TABLE(SEM_MATCH('SELECT * WHERE {
				   ?s1 rdf:type :EMP .
				   OPTIONAL { ?s1 :HIREDATE ?v1 }
				   OPTIONAL { ?s1 :COMM ?v2 }
				   OPTIONAL { ?s1 :DBUNIQUEID ?v3 }
				   OPTIONAL { ?s1 :JOB ?v4 }
				   OPTIONAL { ?s1 :DEPTNO ?v5 }
				   OPTIONAL { ?s1 :SAL ?v6 }
				   OPTIONAL { ?s1 :ENAME ?v7 }
				   OPTIONAL { ?s1 :MGR ?v8 }
				   OPTIONAL { ?s1 :EMPNO ?v9 }
				   }
				   */
						doRDF(statement, conn);
					}
				} else if (conn.getConnectionType() == "ag_sparql_rdf_mode") {
				   /* Test in R with:
				   r <- data.frame(fromJSON(getURL(URLencode('ec2-user@ec2-52-10-0-189.us-west-2.compute.amazonaws.com:5001/rest/native/?query="SELECT ?s ?p ?o  "(lambda x: x)(q)" {?s ?p ?o .}"'),httpheader=c(DB='jdbc:oracle:thin:@129.152.144.84:1521/PDB1.usuniversi01134.oraclecloud.internal',USER='DV_Diamonds',PASS='orcl',MODE='ag_sparql_rdf_mode',MODEL='model',returnDimensions = 'False',returnFor = 'JSON', q="WHERE"),verbose = TRUE))); r
				   */
					System.out.println("Ready for some AllegroGraph processing.");
					if (ReLstmt.charAt(0) == '\'' ) ReLstmt = ReLstmt.substring(1, ReLstmt.length()-1); //This happens for a py program like:
					//sql = "select * from emp"
					//print SQL "" sql
					if (conn.getDebug() == "debug") System.out.println("Remote: " + ReLstmt);
					try {
						ArrayList<ArrayList<String>> agrows = JenaTutorialExamples.example3(ReLstmt);
						System.out.println("----------" + agrows + "\n__________");
						ArrayList<PyObject> rows = new ArrayList<PyObject>();
						ArrayList<PyObject> items = new ArrayList<PyObject>();
						items.add(new PyString("Subject"));
						items.add(new PyString("Predicate"));
						items.add(new PyString("Object"));
						rows.add(new PyTuple(listtoarray(items)));
						for (ArrayList<String> a : agrows) {
							items = new ArrayList<PyObject>();
							for (String s : a) {
								items.add(new PyString(s));
							}
							rows.add(new PyTuple(listtoarray(items)));
						}
						PyObject[] results = listtoarray(rows);
						//put results in array for this tuple object
						array = new PyObject[results.length];
						System.arraycopy(results, 0, array, 0, results.length);

					} catch (Exception e) {
						System.out.println(e);
					} // End try/catch
				}
			} else {
				System.out.println("Connection type must be \"native_mode\", or \"rdf_mode\", not \"" + conn.getConnectionType() + "\"");
			}
		} else if(ReLmode == "SIM") {
			System.out.println("PyTuple sim is: " + ReLstmt);
			ProcessLanguages processLanguage = new ProcessLanguages(conn);
			String sparql = null;
			try { sparql = processLanguage.processSIM(ReLstmt); }
			catch(Exception e1) { System.out.println(e1.getMessage()); }
			if(sparql != null ) runAndOutputTuples(conn, sparql);
		}
	}

	public void doRDF(net.sf.jsqlparser.statement.Statement statement, PyRelConnection conn) {
		if (!flag) {
			v = new SQLVisitor(conn);
			flag = true;
		}
		if (statement instanceof CreateTable) {
			try {
				net.sf.jsqlparser.statement.create.table.CreateTable caststmt =
						(net.sf.jsqlparser.statement.create.table.CreateTable)statement;
				SQLVisitor visitor = new SQLVisitor(conn);
				visitor.doCreateTable(caststmt);
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
				//PyObject[] temp = new PyObject[1];
				//temp[0] = new PyString(e.toString());
				//rows.add(new PyTuple(temp));
			}
		} else if (statement instanceof Insert) {
			try {
				net.sf.jsqlparser.statement.insert.Insert caststmt =
						(net.sf.jsqlparser.statement.insert.Insert)statement;
				SQLVisitor visitor = new SQLVisitor(conn);
				visitor.doInsert(caststmt, conn.getConnectionType());//visitor.doInsert takes 2 arguments now instead of 1 argument
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
				//PyObject[] temp = new PyObject[1];
				//temp[0] = new PyString(e.toString());
				//rows.add(new PyTuple(temp));
			}
		} else if (statement instanceof SubSelect){
			try {
				net.sf.jsqlparser.statement.select.SubSelect caststmt = (net.sf.jsqlparser.statement.select.SubSelect)statement;
				String rdf;
				if (this.relQueryInstancesTypeNames.size() > 0)
				{
					rdf = v.getSelect(caststmt, relQueryInstancesTypeNames, conn.getConnectionType());
				}
				else {
					rdf = v.getSelect(caststmt, null, conn.getConnectionType());
				}
				runAndOutputTuples(conn, rdf);
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
				//PyObject[] temp = new PyObject[1];
				//temp[0] = new PyString(e.toString());
				//rows.add(new PyTuple(temp));
			}
		} else if (statement instanceof Select) {
			try {

				net.sf.jsqlparser.statement.select.Select caststmt = (net.sf.jsqlparser.statement.select.Select)statement;
				String rdf;
				if (this.relQueryInstancesTypeNames.size() > 0)
				{
					rdf = v.getSelect(caststmt, relQueryInstancesTypeNames, conn.getConnectionType());
				}
				else {
					rdf = v.getSelect(caststmt, null, conn.getConnectionType());
				}
				// String rdf = "";
				// an oo query forces the session to be committed first.
				conn.commit_oorel_session();
				runAndOutputTuples(conn, rdf);
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
				//PyObject[] temp = new PyObject[1];
				//temp[0] = new PyString(e.toString());
				//rows.add(new PyTuple(temp));
			}
		} else if (statement instanceof Delete)
		{
			net.sf.jsqlparser.statement.delete.Delete caststmt = (net.sf.jsqlparser.statement.delete.Delete) statement;
			String sqlstmt = "";

			BinaryExpression expression = ((BinaryExpression)caststmt.getWhere());
			Expression left = expression.getLeftExpression();
			Expression right = expression.getRightExpression();
			String left_exp = left + "";
			String right_exp = right + "";
			if(right.toString().charAt(0) == '\'') {
				right_exp = right_exp.substring(1, right_exp.length()-1);
			}

			String table = conn.getTable();
			String this_value = right_exp;
			String this_column = left_exp;
			String rvalue = "";
			String lvalue = "";
			try
			{
				Double.parseDouble(this_value);
				try {
					Integer.parseInt(this_value);
					rvalue  = "a.triple.get_obj_value() = '\"" + this_value + "\"^^<http://www.w3.org/2001/XMLSchema#integer>'";
				} catch(NumberFormatException e) {
					rvalue  = "a.triple.get_obj_value() = '\"" + this_value + "\"^^<http://www.w3.org/2001/XMLSchema#float>'";
				}
			} catch(NumberFormatException e)
			{
				rvalue  = "a.triple.get_obj_value() = '\"" + this_value + "\"^^<http://www.w3.org/2001/XMLSchema#string>'";
			}
			try
			{
				Double.parseDouble(this_column);
				try {
					Integer.parseInt(this_column);
					lvalue  = "a.triple.get_obj_value() = '\"" + this_column + "\"^^<http://www.w3.org/2001/XMLSchema#integer>'";
				} catch(NumberFormatException e) {
					lvalue  = "a.triple.get_obj_value() = '\"" + this_column + "\"^^<http://www.w3.org/2001/XMLSchema#float>'";
				}
			} catch(NumberFormatException e)
			{
				lvalue  = "a.triple.get_obj_value() = '\"" + this_column + "\"^^<http://www.w3.org/2001/XMLSchema#string>'";
			}

			String lcolumn = "a.triple.get_property() = '<" + conn.getNamespace() + this_column + ">'";
			String rcolumn = "a.triple.get_property() = '<" + conn.getNamespace() + this_value + ">'";
			String subject = "a.triple.get_subject() IN (SELECT distinct b.triple.GET_SUBJECT()  from " + conn.getTable() +
					" b where b.triple.get_property() = '<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>' and b.triple.get_obj_value() = '<" +
					conn.getNamespace() + caststmt.getTable() + ">')";

            /* Example: 
            DELETE FROM RDF_CS347_PROF_DATA a
            WHERE ((a.triple.get_property() = '<http://www.example.org/people.owl#VAL1>' AND a.triple.get_obj_value() = '"one"^^<http://www.w3.org/2001/XMLSchema#string>')
            OR (a.triple.get_property() = '<http://www.example.org/people.owl#one>' AND a.triple.get_obj_value() = '"VAL1"^^<http://www.w3.org/2001/XMLSchema#string>'))
            AND a.triple.get_subject() IN
            (SELECT distinct b.triple.GET_SUBJECT()
               from RDF_CS347_PROF_DATA b
               where b.triple.get_property() = '<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>' and b.triple.get_obj_value() = '<http://www.example.org/people.owl#TEST_DELETE>')
            */

			sqlstmt = "DELETE FROM " + table + " a WHERE ((" + lcolumn + " AND " + rvalue + ") OR (" + rcolumn + " AND " + lvalue + ")) AND " + subject;
			if (conn.getDebug() == "debug") System.out.println();
			if (conn.getDebug() == "debug") System.out.println(sqlstmt);
			if (conn.getDebug() == "debug") System.out.println();
			try{ conn.executeStatement(sqlstmt);
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
		} else if (statement instanceof Update) {
			net.sf.jsqlparser.statement.update.Update caststmt = (net.sf.jsqlparser.statement.update.Update) statement;
			String sqlstmt = "";

			String table = conn.getTable();
			String subject = "a.triple.get_subject() IN (SELECT distinct b.triple.GET_SUBJECT()  from " + conn.getTable() +
					" b where b.triple.get_obj_value() = '<" +
					conn.getNamespace() + ">')";
			System.out.println("In Update\ntable is : " + table);
			System.out.println("subject is : " + subject);
/*
		    sqlstmt = "";
		    List cols = caststmt.getColumns();
		    List expr = caststmt.getExpressions();
		    
		    if(network.equals("remote")) {
			  String col_query = "";
			  for(int i = 0; i < cols.size(); i++){
				if(i+1 == cols.size()){
				    col_query += cols.get(i) + " = " + expr.get(i);
				}
				else {
				    col_query += cols.get(i) + " = " + expr.get(i) + ", ";
				}
			  }
			  
			  sqlstmt = "UPDATE " + caststmt.getTable() + " SET " + col_query + " WHERE " + caststmt.getWhere();

			  stmt.execute(sqlstmt);
		    }
		    else {
			  String user = "CS345_cjr739";
			  String table = caststmt.getTable() + "_RDF_DATA";
			  String declaration = "SDO_RDF_TRIPLE_S('" + caststmt.getTable() + "_" + user + "', ";
			  String subject = "a.triple.get_subject()";

			  // could have multiple where clauses!
			  //String right_exp = right + "";
			  //if(right.toString().charAt(0) == '\'') {
			  //right_exp = right_exp.substring(1, right_exp.length()-1);
			  //}


			  for(int i = 0; i < cols.size(); i++) {
				// UDPATE TABLE SET __property__ = __object__  ...
				String property = website + cols.get(i);
				String object   = website + expr.get(i);
				String triple   = declaration + subject + ", '" + property + "', '" + object + "') ";

				// ... WHERE __left__ = __right__
				BinaryExpression expression = ((BinaryExpression)caststmt.getWhere());
				Expression left  = expression.getLeftExpression();
				Expression right = expression.getRightExpression();
				
				String where_expr = website + left;
				String where_val  = website + right;
				//System.out.println("left: " + where_expr);;
				//System.out.println("right: " + where_val);
				
				String whereclause = "WHERE (a.triple.get_property() = '" + where_expr + "' OR a.triple.get_property() = '<" + where_expr + ">') ";
				whereclause +=         "AND (a.triple.get_obj_value() = '" + where_val + "' OR a.triple.get_obj_value() = '<" + where_val + ">')";

				sqlstmt = "UPDATE " + table + " a SET a.triple = " + triple + whereclause;

				if (conn.getDebug() == "debug") System.out.println();
				if (conn.getDebug() == "debug") System.out.println(sqlstmt);
				if (conn.getDebug() == "debug") System.out.println();

				stmt.execute(sqlstmt);
			  }
		    }
*/
		} else if (statement instanceof Drop) {
			net.sf.jsqlparser.statement.drop.Drop caststmt = (net.sf.jsqlparser.statement.drop.Drop) statement;
			String sqlstmt = "";

			String table = conn.getTable();
			String subject = "a.triple.get_subject() IN (SELECT distinct b.triple.GET_SUBJECT()  from " + conn.getTable() +
					" b where b.triple.get_obj_value() = '<" +
					conn.getNamespace() + caststmt.getName() + ">')";

			sqlstmt = "DELETE FROM " + table + " a WHERE " + subject;
			if (conn.getDebug() == "debug") System.out.println();
			if (conn.getDebug() == "debug") System.out.println(sqlstmt);
			if (conn.getDebug() == "debug") System.out.println();
			try{ conn.executeStatement(sqlstmt);
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}
	}

	public void runAndOutputTuples(PyRelConnection conn, String ReLstmt) {
		ArrayList<PyObject> rows = new ArrayList<PyObject>();
		ResultSet rs = null;
		try{
			try {
				int dbuniqueid_column_idx = 0;
				if(ReLstmt.trim().toUpperCase().indexOf("SELECT") != 0) {
					String[] stmts = ReLstmt.split("~");
					for (String s : stmts) {
						if (conn.getDebug() == "debug") System.out.println("stmt : " + s);
						if(s.length() > 5 && s.trim().indexOf("--") != 0) conn.executeStatement(s);
					}

					/**
					 *   Example Java Batch
					 */
                    /*EvalService<File> dirServer = new EvalService<File>(new File("/"));
                    for (File dir : dirServer) {
                        for(File file :dir.listFiles()) {
                            System.out.println("Listing files");
                            if(file.length() > 1000) {
                                System.out.println(file.getPath());
                            }
                        }
                    }*/
				}
				else {
					rs = conn.executeQuery(ReLstmt);
					ResultSetMetaData rsmd = rs.getMetaData();
					int cc = rsmd.getColumnCount();
					PyObject[] temp;
					ArrayList<PyObject> columns = new ArrayList<PyObject>();
					int nTuple = cc;
					int dbuniqueid_idx = -1;
					for (int i = 1; i <= cc; i++) {
						columns.add(new PyString(rsmd.getColumnLabel(i)));
						if(rsmd.getColumnName(i).equals("DBUNIQUEID"))
						{
							dbuniqueid_idx = i - 1;
						}
					}

					// If we are not returning class instance's, then 
					// the first tuple should be a list of the column names. 
					if(this.relQueryInstancesType.size() == 0)
					{
						temp = listtoarray(columns);
						rows.add(new PyTuple(temp));
					}

					while (rs.next()) {
						ArrayList<PyObject> items = new ArrayList<PyObject>();
						for (int i = 1; i <= cc; i++) {   // cc is the column count
							int type = rsmd.getColumnType(i);
							if (rs.getString(i) == null) items.add(new PyString("null"));
/* The commented code is just not working correctly
						 else if (type == Types.NUMERIC) {
							if (rsmd.getScale(i) == 0) items.add(new PyInteger(rs.getInt(i)));
							else items.add(new PyFloat(rs.getLong(i)));
						 }
						 else if (type == Types.DECIMAL ||
							type == Types.REAL ||
							type == Types.FLOAT ||
							type == Types.DOUBLE) items.add(new PyFloat(rs.getLong(i)));
						 else if (
							type == Types.BIT ||
							type == Types.TINYINT ||
							type == Types.SMALLINT ||
							type == Types.INTEGER ||
							type == Types.BIGINT ) items.add(new PyInteger(rs.getInt(i)));
*/
							else {
/*
	Can't get this to work because import org.apache.commons.lang3.math; above fails even though
	I put commons-lang3-3.0.jar in extlibs and added it to build.xml. It would probably be a better
	solution than the try blocks below, but for the time being, they work.
						   if(NumberUtils.isNumber(s)) {
							  if(NumberUtils.isDigits(s)) items.add(new PyInteger(rs.getInt(i)));
							  else items.add(new PyFloat(rs.getLong(i)));
						   }
						   else {
							  String s = rs.getString(i) == null ? "": rs.getString(i);
							  items.add(new PyString(s));
						   }
*/
								String rsString = rs.getString(i).replace(conn.getNamespace(),"");
								try
								{
									Double.parseDouble(rsString);
									try {
										Integer.parseInt(rsString);
										items.add(new PyInteger(Integer.parseInt(rsString)));
									} catch(NumberFormatException e) {
										items.add(new PyFloat(Float.parseFloat(rsString)));
									}
								}
								catch(NumberFormatException e)
								{
									items.add(new PyString(rsString));
								}
							}
						}

						// Return tuples of the data or return class instances with the data populated in them.
						if(this.relQueryInstancesType.size() == 0)
						{
							temp = listtoarray(items);
							rows.add(new PyTuple(temp));
						}
						else{ // return the instances of a class.
							// So the first type in the query is the type that is actually returned.
							// then pointers to the other types are added to the first types dictionary.
							// so select * from a, b, c where a.id == b.id and a.id == c.id;
							// will return instance of A and in a's dictionary there will be A.b -> instance of b, etc.
							// Create instances for each table we queried data from.
							PyObjectDerived top_instance = createInstanceFromResults(conn, relQueryInstancesType.get(0), columns, items);
							ConcurrentMap<Object, PyObject> main_inst_dict;
							main_inst_dict = ((PyStringMap)top_instance.fastGetDict()).getTable();
							for(int i = 1; i < relQueryInstancesType.size(); i++)
							{
								PyObjectDerived new_instance = createInstanceFromResults(conn, relQueryInstancesType.get(i), columns, items);
								main_inst_dict.put(relQueryInstancesType.get(i).getName(), new_instance);
							}
							rows.add(top_instance);

						}
					}
					rs.close();
				}
			} catch (Exception e) {
				try { rs.close(); } catch (Exception ignore) { }
				PyObject[] temp = new PyObject[1];
				temp[0] = new PyString(e.toString());
				rows.add(new PyTuple(temp));
			}
		} finally {
			try { rs.close(); } catch (Exception ignore) { }
		}
		//a lot of conversion going on here. . .
		PyObject[] results = listtoarray(rows);
		//put results in array for this tuple object
		array = new PyObject[results.length];
		System.arraycopy(results, 0, array, 0, results.length);
	}



	/* A helper for creating user object instances.
       This is useful for returning instance during joins. This guy expects the columns associated with
       each type to have the types name_ appended to the front of the column name. 
    */
	private PyObjectDerived createInstanceFromResults(PyRelConnection connection, PyType instance_type, ArrayList<PyObject> columns, ArrayList<PyObject> data)
	{
		String typeName = instance_type.getName()+"_";
		PyObjectDerived instance = new PyObjectDerived(instance_type);
		ConcurrentMap<Object, PyObject> inst_dict;
		inst_dict = ((PyStringMap)instance.fastGetDict()).getTable();
		// Add data as we find columns that should represent this type.
		for(int i = 0; i < columns.size(); i++) {
			String column = ((PyString)columns.get(i)).toString();
			if(column.startsWith(typeName)) {
				// If we found the dbuniqueid, check to see this id is already in our runtime's session, if so just return that item.
				if(column.equals(typeName+"DBUNIQUEID")) {
					PyInteger unique_id = (PyInteger)data.get(i);
					PyObjectDerived possible_instance_in_session = (PyObjectDerived)connection.getInstance(unique_id.getValue());
					if(possible_instance_in_session != null) {
						return possible_instance_in_session;
					}
				}
				inst_dict.put(columns.get(i).toString().replaceFirst(typeName, ""), data.get(i));

			}
		}
		return instance;


	}

	//helper to convert lists to arrays

	private PyObject[] listtoarray(ArrayList<PyObject> a) {
		PyObject[] results = new PyObject[a.size()];
		int iter = 0;
		for (PyObject pt : a) {
			results[iter] = pt;
			iter++;
		}
		return results;

	}

// End ReL addition

	private static PyTuple fromArrayNoCopy(PyObject[] elements) {
		return new PyTuple(elements, false);
	}

	List<PyObject> getList() {
		if (cachedList == null) {
			cachedList = Arrays.asList(array);
		}
		return cachedList;
	}

	@ExposedNew
	final static PyObject tuple_new(PyNewWrapper new_, boolean init, PyType subtype,
									PyObject[] args, String[] keywords) {
		ArgParser ap = new ArgParser("tuple", args, keywords, new String[] {"sequence"}, 0);
		PyObject S = ap.getPyObject(0, null);
		if (new_.for_type == subtype) {
			if (S == null) {
				return EMPTY_TUPLE;
			}
			if (S instanceof PyTupleDerived) {
				return new PyTuple(((PyTuple) S).getArray());
			}
			if (S instanceof PyTuple) {
				return S;
			}
			return fromArrayNoCopy(Py.make_array(S));
		} else {
			if (S == null) {
				return new PyTupleDerived(subtype, Py.EmptyObjects);
			}
			return new PyTupleDerived(subtype, Py.make_array(S));
		}
	}



	/**
	 * Return a new PyTuple from an iterable.
	 *
	 * Raises a TypeError if the object is not iterable.
	 *
	 * @param iterable an iterable PyObject
	 * @return a PyTuple containing each item in the iterable
	 */
	public static PyTuple fromIterable(PyObject iterable) {
		return fromArrayNoCopy(Py.make_array(iterable));
	}

	protected PyObject getslice(int start, int stop, int step) {
		if (step > 0 && stop < start) {
			stop = start;
		}
		int n = sliceLength(start, stop, step);
		PyObject[] newArray = new PyObject[n];

		if (step == 1) {
			System.arraycopy(array, start, newArray, 0, stop - start);
			return fromArrayNoCopy(newArray);
		}
		for (int i = start, j = 0; j < n; i += step, j++) {
			newArray[j] = array[i];
		}
		return fromArrayNoCopy(newArray);
	}

	protected PyObject repeat(int count) {
		if (count < 0) {
			count = 0;
		}
		int size = size();
		if (size == 0 || count == 1) {
			if (getType() == TYPE) {
				// Since tuples are immutable, we can return a shared copy in this case
				return this;
			}
			if (size == 0) {
				return EMPTY_TUPLE;
			}
		}

		int newSize = size * count;
		if (newSize / size != count) {
			throw Py.MemoryError("");
		}

		PyObject[] newArray = new PyObject[newSize];
		for (int i = 0; i < count; i++) {
			System.arraycopy(array, 0, newArray, i * size, size);
		}
		return fromArrayNoCopy(newArray);
	}

	@Override
	public int __len__() {
		return tuple___len__();
	}

	@ExposedMethod(doc = BuiltinDocs.tuple___len___doc)
	final int tuple___len__() {
		return size();
	}

	@ExposedMethod(doc = BuiltinDocs.tuple___contains___doc)
	final boolean tuple___contains__(PyObject o) {
		return super.__contains__(o);
	}

	@ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___ne___doc)
	final PyObject tuple___ne__(PyObject o) {
		return super.__ne__(o);
	}

	@ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___eq___doc)
	final PyObject tuple___eq__(PyObject o) {
		return super.__eq__(o);
	}

	@ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___gt___doc)
	final PyObject tuple___gt__(PyObject o) {
		return super.__gt__(o);
	}

	@ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___ge___doc)
	final PyObject tuple___ge__(PyObject o) {
		return super.__ge__(o);
	}

	@ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___lt___doc)
	final PyObject tuple___lt__(PyObject o) {
		return super.__lt__(o);
	}

	@ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___le___doc)
	final PyObject tuple___le__(PyObject o) {
		return super.__le__(o);
	}

	@Override
	public PyObject __add__(PyObject generic_other) {
		return tuple___add__(generic_other);
	}

	@ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___add___doc)
	final PyObject tuple___add__(PyObject generic_other) {
		PyTuple sum = null;
		if (generic_other instanceof PyTuple) {
			PyTuple other = (PyTuple) generic_other;
			PyObject[] newArray = new PyObject[array.length + other.array.length];
			System.arraycopy(array, 0, newArray, 0, array.length);
			System.arraycopy(other.array, 0, newArray, array.length, other.array.length);
			sum = fromArrayNoCopy(newArray);
		}
		return sum;
	}

	@Override
	public PyObject __mul__(PyObject o) {
		return tuple___mul__(o);
	}

	@ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___mul___doc)
	final PyObject tuple___mul__(PyObject o) {
		if (!o.isIndex()) {
			return null;
		}
		return repeat(o.asIndex(Py.OverflowError));
	}

	@Override
	public PyObject __rmul__(PyObject o) {
		return tuple___rmul__(o);
	}

	@ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___rmul___doc)
	final PyObject tuple___rmul__(PyObject o) {
		if (!o.isIndex()) {
			return null;
		}
		return repeat(o.asIndex(Py.OverflowError));
	}

	@Override
	public PyObject __iter__() {
		return tuple___iter__();
	}

	@ExposedMethod(doc = BuiltinDocs.tuple___iter___doc)
	public PyObject tuple___iter__() {
		return new PyFastSequenceIter(this);
	}

	@ExposedMethod(defaults = "null", doc = BuiltinDocs.tuple___getslice___doc)
	final PyObject tuple___getslice__(PyObject s_start, PyObject s_stop, PyObject s_step) {
		return seq___getslice__(s_start, s_stop, s_step);
	}

	@ExposedMethod(doc = BuiltinDocs.tuple___getitem___doc)
	final PyObject tuple___getitem__(PyObject index) {
		PyObject ret = seq___finditem__(index);
		if (ret == null) {
			throw Py.IndexError("index out of range: " + index);
		}
		return ret;
	}

	@ExposedMethod(doc = BuiltinDocs.tuple___getnewargs___doc)
	final PyTuple tuple___getnewargs__() {
		return new PyTuple(new PyTuple(getArray()));
	}

	@Override
	public PyTuple __getnewargs__() {
		return tuple___getnewargs__();
	}

	@Override
	public int hashCode() {
		return tuple___hash__();
	}

	@ExposedMethod(doc = BuiltinDocs.tuple___hash___doc)
	final int tuple___hash__() {
		// strengthened hash to avoid common collisions. from CPython
		// tupleobject.tuplehash. See http://bugs.python.org/issue942952
		int y;
		int len = size();
		int mult = 1000003;
		int x = 0x345678;
		while (--len >= 0) {
			y = array[len].hashCode();
			x = (x ^ y) * mult;
			mult += 82520 + len + len;
		}
		return x + 97531;
	}

	private String subobjRepr(PyObject o) {
		if (o == null) {
			return "null";
		}
		return o.__repr__().toString();
	}

	@Override
	public String toString() {
		return tuple___repr__();
	}

	@ExposedMethod(doc = BuiltinDocs.tuple___repr___doc)
	final String tuple___repr__() {
		StringBuilder buf = new StringBuilder("(");
		if(array != null) {
			for (int i = 0; i < array.length - 1; i++) {
				buf.append(subobjRepr(array[i]));
				buf.append(", ");
			}
			if (array.length > 0) {
				buf.append(subobjRepr(array[array.length - 1]));
			}
			if (array.length == 1) {
				buf.append(",");
			}
		}
		buf.append(")");
		return buf.toString();
	}

	public List subList(int fromIndex, int toIndex) {
		if (fromIndex < 0 || toIndex > size()) {
			throw new IndexOutOfBoundsException();
		} else if (fromIndex > toIndex) {
			throw new IllegalArgumentException();
		}
		PyObject elements[] = new PyObject[toIndex - fromIndex];
		for (int i = 0, j = fromIndex; i < elements.length; i++, j++) {
			elements[i] = array[j];
		}
		return new PyTuple(elements);
	}

	public Iterator iterator() {
		return new Iterator() {

			private final Iterator<PyObject> iter = getList().iterator();

			public void remove() {
				throw new UnsupportedOperationException();
			}

			public boolean hasNext() {
				return iter.hasNext();
			}

			public Object next() {
				return iter.next().__tojava__(Object.class);
			}
		};
	}

	public boolean add(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection coll) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection coll) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection coll) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public Object set(int index, Object element) {
		throw new UnsupportedOperationException();
	}

	public void add(int index, Object element) {
		throw new UnsupportedOperationException();
	}

	public Object remove(int index) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(int index, Collection c) {
		throw new UnsupportedOperationException();
	}

	public ListIterator listIterator() {
		return listIterator(0);
	}

	public ListIterator listIterator(final int index) {
		return new ListIterator() {

			private final ListIterator<PyObject> iter = getList().listIterator(index);

			public boolean hasNext() {
				return iter.hasNext();
			}

			public Object next() {
				return iter.next().__tojava__(Object.class);
			}

			public boolean hasPrevious() {
				return iter.hasPrevious();
			}

			public Object previous() {
				return iter.previous().__tojava__(Object.class);
			}

			public int nextIndex() {
				return iter.nextIndex();
			}

			public int previousIndex() {
				return iter.previousIndex();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

			public void set(Object o) {
				throw new UnsupportedOperationException();
			}

			public void add(Object o) {
				throw new UnsupportedOperationException();
			}
		};
	}

	protected String unsupportedopMessage(String op, PyObject o2) {
		if (op.equals("+")) {
			return "can only concatenate tuple (not \"{2}\") to tuple";
		}
		return super.unsupportedopMessage(op, o2);
	}

	public void pyset(int index, PyObject value) {
		throw Py.TypeError("'tuple' object does not support item assignment");
	}

	@Override
	public boolean contains(Object o) {
		return getList().contains(Py.java2py(o));
	}

	@Override
	public boolean containsAll(Collection c) {
		if (c instanceof PyList) {
			return getList().containsAll(((PyList)c).getList());
		} else if (c instanceof PyTuple) {
			return getList().containsAll(((PyTuple)c).getList());
		} else {
			return getList().containsAll(new PyList(c));
		}
	}

	public int count(PyObject value) {
		return tuple_count(value);
	}

	@ExposedMethod(doc = BuiltinDocs.tuple_count_doc)
	final int tuple_count(PyObject value) {
		int count = 0;
		for (PyObject item : array) {
			if (item.equals(value)) {
				count++;
			}
		}
		return count;
	}

	public int index(PyObject value) {
		return index(value, 0);
	}

	public int index(PyObject value, int start) {
		return index(value, start, size());
	}

	public int index(PyObject value, int start, int stop) {
		return tuple_index(value, start, stop);
	}

	@ExposedMethod(defaults = {"null", "null"}, doc = BuiltinDocs.tuple_index_doc)
	final int tuple_index(PyObject value, PyObject start, PyObject stop) {
		int startInt = start == null ? 0 : PySlice.calculateSliceIndex(start);
		int stopInt = stop == null ? size() : PySlice.calculateSliceIndex(stop);
		return tuple_index(value, startInt, stopInt);
	}

	final int tuple_index(PyObject value, int start, int stop) {
		int validStart = boundToSequence(start);
		int validStop = boundToSequence(stop);
		for (int i = validStart; i < validStop; i++) {
			if (array[i].equals(value)) {
				return i;
			}
		}
		throw Py.ValueError("tuple.index(x): x not in list");
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (other instanceof PyObject) {
			return _eq((PyObject)other).__nonzero__();
		}
		if (other instanceof List) {
			return other.equals(this);
		}
		return false;
	}

	@Override
	public Object get(int index) {
		return array[index].__tojava__(Object.class);
	}

	@Override
	public PyObject[] getArray() {
		return array;
	}

	@Override
	public int indexOf(Object o) {
		return getList().indexOf(Py.java2py(o));
	}

	@Override
	public boolean isEmpty() {
		return array.length == 0;
	}

	@Override
	public int lastIndexOf(Object o) {
		return getList().lastIndexOf(Py.java2py(o));
	}

	@Override
	public void pyadd(int index, PyObject element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean pyadd(PyObject o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PyObject pyget(int index) {
		return array[index];
	}

	@Override
	public void remove(int start, int stop) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return array.length;
	}

	@Override
	public Object[] toArray() {
		Object[] converted = new Object[array.length];
		for (int i = 0; i < array.length; i++) {
			converted[i] = array[i].__tojava__(Object.class);
		}
		return converted;
	}

	@Override
	public Object[] toArray(Object[] converted) {
		Class<?> type = converted.getClass().getComponentType();
		if (converted.length < array.length) {
			converted = (Object[])Array.newInstance(type, array.length);
		}
		for (int i = 0; i < array.length; i++) {
			converted[i] = type.cast(array[i].__tojava__(type));
		}
		if (array.length < converted.length) {
			for (int i = array.length; i < converted.length; i++) {
				converted[i] = null;
			}
		}
		return converted;
	}
}
