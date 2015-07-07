package com.jboss.examples.brms;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;

import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.model.ImportFilter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class JPMMLExecutionTest {
	
	private static final Map<String, String> rawValues;
    static
    {
    	rawValues = new HashMap<String, String>();
    	rawValues.put("age", "25");
    	rawValues.put("marital status", "m");
    	rawValues.put("dependents", "1");
    }

	public static void main(String[] args) {

		try {
			System.out.println("READING SCORECARD FROM PMML FILE");
			System.out.println(evaluate());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String evaluate() {

		// Load PMML schema version
		PMML pmml = null;
		InputStream is = JPMMLExecutionTest.class.getResourceAsStream("/ScorecardPMML.pmml");
		try {
			Source transformedSource = ImportFilter.apply(new InputSource(is));
			pmml = JAXBUtil.unmarshalPMML(transformedSource);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Model evaluator instantiation
		ModelEvaluatorFactory modelEvaluatorFactory = ModelEvaluatorFactory
				.newInstance();
		ModelEvaluator<?> modelEvaluator = modelEvaluatorFactory
				.newModelManager(pmml);
		Evaluator evaluator = (Evaluator) modelEvaluator;

		List<FieldName> activeFields = evaluator.getActiveFields();
		List<FieldName> targetFields = evaluator.getTargetFields();
		List<FieldName> outputFields = evaluator.getOutputFields();

		System.out.println("Active fields: " + activeFields.toString());
		System.out.println("Target fields: " + targetFields.toString());
		System.out.println("Output fields: " + outputFields.toString());

		// Active fields[marital status, age, dependents]
		// Target fields[income]
		// Output fields[output_1, neighbor1, neighbor2, neighbor3]

		// Preparation of field values
		// Beware of conflicts with Guava version (13 for Drools, 14 for JPMML)
		Map<FieldName, FieldValue> arguments = new LinkedHashMap<FieldName, FieldValue>();

		for (FieldName activeField : activeFields) {
			Object rawValue = rawValues.get(activeField.toString());
			if (rawValue != null) {
				FieldValue activeValue = evaluator.prepare(activeField,
						rawValue);
				arguments.put(activeField, activeValue);
			}
		}

		// Scoring
		Map<FieldName, ?> results = evaluator.evaluate(arguments);
		FieldName targetName = evaluator.getTargetField();
		Object targetValue = results.get(targetName);

		return (targetValue != null) ? targetValue.toString() : "null";
	}

}
