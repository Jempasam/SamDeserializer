package jempasam.data.deserializer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;

import jempasam.data.modifier.TemplateDataModifier;
import jempasam.logger.SLogger;
import jempasam.samstream.text.TokenizerConfig;
import jempasam.samstream.text.TokenizerSStream;

public class DataDeserializers {
	
	public static DataDeserializer createStrobjoDS(SLogger logger) {
		TokenizerConfig config=new TokenizerConfig();
		config.cutChars=" \n\r\t";
		config.uniqueChars=":(),";
		config.escapeAroundChars="\"'";
		config.commentChars="#";
		StrobjoDataDeserializer ret=new StrobjoDataDeserializer( (i)->new TokenizerSStream(i, config), logger );
		return ret;
	}
	
	public static DataDeserializer createJSONLikeStrobjoDS(SLogger logger) {
		TokenizerConfig config=new TokenizerConfig();
		config.cutChars=" \n\r\t";
		config.uniqueChars=":{},";
		config.escapeAroundChars="\"'";
		config.commentChars="#";
		StrobjoDataDeserializer ret=new StrobjoDataDeserializer( (i)->new TokenizerSStream(i, config), logger );
		ret.setCloseToken("}");
		ret.setOpenToken("{");
		ret.setSeparatorToken(",");
		ret.setAffectationToken(":");
		return ret;
	}
	
	public static DataDeserializer createYAMLLikeChardentDS(SLogger logger) {
		TokenizerConfig config=new TokenizerConfig();
		config.cutChars=" \r";
		config.uniqueChars=":\n\t";
		config.escapeAroundChars="\"'";
		config.commentChars="#";
		ChardentDataDeserializer ret=new ChardentDataDeserializer( config::create, logger );
		ret.setAffectationToken(":");
		ret.setIndentorToken("\t");
		ret.setSeparatorToken("\n");
		return ret;
	}
	
	public static DataDeserializer createIndentedBaliseDS(SLogger logger) {
		TokenizerConfig config=new TokenizerConfig();
		config.cutChars=" \r";
		config.uniqueChars=":\t(),\n";
		config.escapeAroundChars="\"'";
		config.commentChars="#";
		ChardentBaliseDataDeserializer ret=new ChardentBaliseDataDeserializer( config::create, logger );
		ret.affectationToken=":";
		ret.openToken="(";
		ret.closeToken=")";
		ret.separatorToken=",";
		ret.indentorToken="\t";
		return ret;
	}
	
	public static DataDeserializer createSquareIndentedBaliseDS(SLogger logger) {
		TokenizerConfig config=new TokenizerConfig();
		config.cutChars=" \r";
		config.uniqueChars=":\t[],\n";
		config.escapeAroundChars="\"'";
		config.commentChars="#";
		ChardentBaliseDataDeserializer ret=new ChardentBaliseDataDeserializer( config::create, logger );
		ret.affectationToken=":";
		ret.openToken="[";
		ret.closeToken="]";
		ret.separatorToken=",";
		ret.indentorToken="\t";
		return ret;
	}
	
	public static DataDeserializer createSGMLLikeBaliseDS(SLogger logger) {
		TokenizerConfig config=new TokenizerConfig();
		config.cutChars=" \n\r\t";
		config.uniqueChars="<>=;/";
		config.escapeAroundChars="\"'";
		config.commentChars="#";
		BaliseDataDeserializer ret=new BaliseDataDeserializer( (i)->new TokenizerSStream(i, config), logger );
		return ret;
	}
	
	public static DataDeserializer createBoxLikeBaliseDS(SLogger logger) {
		TokenizerConfig config=new TokenizerConfig();
		config.cutChars=" \n\r\t";
		config.uniqueChars="[:=,]";
		config.escapeAroundChars="\"'";
		config.commentChars="#";
		BaliseDataDeserializer ret=new BaliseDataDeserializer( (i)->new TokenizerSStream(i, config), logger );
		ret.setPermissive(true);
		ret.setSeparatorToken(",");
		ret.setCloseBaliseToken(":");
		ret.setOpenBaliseToken("[");
		ret.setEndBaliseToken("]");
		return ret;
	}
	
	public static DataDeserializer createStructLikeStrobjoDS(SLogger logger) {
		TokenizerConfig config=new TokenizerConfig();
		config.cutChars=" \n\r\t";
		config.uniqueChars="{}=;";
		config.escapeAroundChars="\"'";
		config.commentChars="#";
		StrobjoDataDeserializer ret=new StrobjoDataDeserializer( (i)->new TokenizerSStream(i, config), logger );
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
