package jempasam.data.deserializer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;

import jempasam.data.modifier.TemplateDataModifier;
import jempasam.logger.SLogger;
import jempasam.textanalyzis.tokenizer.impl.InputStreamSimpleTokenizer;

public class DataDeserializers {
	
	public static DataDeserializer createStrobjoDS(SLogger logger) {
		StrobjoDataDeserializer ret=new StrobjoDataDeserializer(
				(i)->{
					InputStreamSimpleTokenizer r=new InputStreamSimpleTokenizer(i," \n\r\t","():,","\"'");
					r.setComment("#");
					return r;
				},
				logger
				);
		return ret;
	}
	
	public static DataDeserializer createJSONLikeStrobjoDS(SLogger logger) {
		StrobjoDataDeserializer ret=new StrobjoDataDeserializer(
				(i)->{
					InputStreamSimpleTokenizer r=new InputStreamSimpleTokenizer(i," \n\r\t","{}:,","\"'");
					r.setComment("#");
					return r;
				},
				logger
				);
		ret.setCloseToken("}");
		ret.setOpenToken("{");
		ret.setSeparatorToken(",");
		ret.setAffectationToken(":");
		return ret;
	}
	
	public static DataDeserializer createSGMLLikeBaliseDS(SLogger logger) {
		BaliseDataDeserializer ret=new BaliseDataDeserializer(
				(i)->{
					InputStreamSimpleTokenizer r=new InputStreamSimpleTokenizer(i," \n\r\t","<>=;/","\"'");
					r.setComment("#");
					return r;
				},
				logger
				);
		return ret;
	}
	
	public static DataDeserializer createBoxLikeBaliseDS(SLogger logger) {
		BaliseDataDeserializer ret=new BaliseDataDeserializer(
				(i)->{
					InputStreamSimpleTokenizer r=new InputStreamSimpleTokenizer(i," \n\r\t","[:=,]","\"'");
					r.setComment("#");
					return r;
				},
				logger
				);
		ret.setPermissive(true);
		ret.setSeparatorToken(",");
		ret.setCloseBaliseToken(":");
		ret.setOpenBaliseToken("[");
		ret.setEndBaliseToken("]");
		return ret;
	}
	
	public static DataDeserializer createStructLikeStrobjoDS(SLogger logger) {
		StrobjoDataDeserializer ret=new StrobjoDataDeserializer(
				(i)->{
					InputStreamSimpleTokenizer r=new InputStreamSimpleTokenizer(i," \n\r\t","{}=;","\"'");
					r.setComment("#");
					return r;
				},
				logger
				);
		ret.setCloseToken("}");
		ret.setOpenToken("{");
		ret.setSeparatorToken(";");
		ret.setAffectationToken("=");
		return ret;
	}
	
	public static TemplateDataModifier createVerboseTemplateDM(SLogger logger) {
		return new TemplateDataModifier(logger, "@template-", "-", "..", "@");
	}
	
	public static TemplateDataModifier createLightTemplateDM(SLogger logger) {
		return new TemplateDataModifier(logger, "@@", "-", "..", "@");
	}
	
	public static DataDeserializer createCompleteTemplateJsonLikeDS(SLogger logger) {
		TemplateDataModifier templateDM=createLightTemplateDM(logger);
		HashMap<String, String> vars=new HashMap<>();
		templateDM.setVariableHolder(vars);
		
		DataDeserializer deserializer=createJSONLikeStrobjoDS(logger);
		ModifiersDataDeserializer templated=new ModifiersDataDeserializer(deserializer,Arrays.asList(templateDM));
		OnLoadDataDeserializer onload=new OnLoadDataDeserializer(templated);
		
		onload.register((dd)->{
			vars.put("time", DateTimeFormatter.ISO_LOCAL_TIME.format(LocalTime.now()));
			vars.put("date", DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now()));
			vars.put("name", System.getProperty("user.name"));
		});
		return onload;
	}
}
