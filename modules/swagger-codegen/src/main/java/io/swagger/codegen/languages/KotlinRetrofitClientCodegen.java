package io.swagger.codegen.languages;

import io.swagger.codegen.*;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Swagger;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class KotlinRetrofitClientCodegen extends AbstractKotlinCodegen {
    static Logger LOGGER = LoggerFactory.getLogger(KotlinRetrofitClientCodegen.class);

    protected String groupId = "com.robotsandpencils";
    protected String artifactId = "kotlin-retrofit2-client";
    protected String artifactVersion = "1.0.0";
    protected String sourceFolder = "src/main/kotlin";
    protected String packageName = "io.swagger.client";
    protected String apiDocPath = "docs/";
    protected String modelDocPath = "docs/";

    /**
     * Constructs an instance of `KotlinRetrofitClientCodegen`.
     */
    public KotlinRetrofitClientCodegen() {
        super();

        //sortParamsByRequiredFlag = false;

        outputFolder = "generated-code" + File.separator + "kotlin-retrofit2-client";
        modelTemplateFiles.put("model.mustache", ".kt");
        apiTemplateFiles.put("api.mustache", ".kt");
        //modelDocTemplateFiles.put("model_doc.mustache", ".md");
        //apiDocTemplateFiles.put("api_doc.mustache", ".md");
        embeddedTemplateDir = templateDir = "kotlin-retrofit2-client";
        apiPackage = packageName + ".apis";
        modelPackage = packageName + ".models";

        typeMapping.put("binary", "Array<Byte>");
        typeMapping.put("Date", "String");
        typeMapping.put("DateTime", "String");

        instantiationTypes.put("array", "arrayOf");
        instantiationTypes.put("list", "arrayOf");
        instantiationTypes.put("map", "mapOf");
        importMapping.put("Date", "java.util.Date");
        importMapping.put("Timestamp", "java.sql.Timestamp");
        importMapping.put("DateTime", "java.time.LocalDateTime");
        importMapping.put("LocalDateTime", "java.time.LocalDateTime");
        importMapping.put("LocalDate", "java.time.LocalDate");
        importMapping.put("LocalTime", "java.time.LocalTime");

        cliOptions.clear();
        cliOptions.add(new CliOption(CodegenConstants.SOURCE_FOLDER, CodegenConstants.SOURCE_FOLDER_DESC).defaultValue(sourceFolder));
        cliOptions.add(new CliOption(CodegenConstants.PACKAGE_NAME, "Client package name (e.g. io.swagger).").defaultValue(this.packageName));
        cliOptions.add(new CliOption(CodegenConstants.GROUP_ID, "Client package's organization (i.e. maven groupId).").defaultValue(groupId));
        cliOptions.add(new CliOption(CodegenConstants.ARTIFACT_ID, "Client artifact id (name of generated jar).").defaultValue(artifactId));
        cliOptions.add(new CliOption(CodegenConstants.ARTIFACT_VERSION, "Client package version.").defaultValue(artifactVersion));
    }

    public CodegenType getTag() {
        return CodegenType.CLIENT;
    }

    public String getName() {
        return "kotlin-retrofit2";
    }

    public String getHelp() {
        return "Generates a kotlin client.";
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public void setArtifactVersion(String artifactVersion) {
        this.artifactVersion = artifactVersion;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setSourceFolder(String sourceFolder) {
        this.sourceFolder = sourceFolder;
    }

    @Override
    public void processOpts() {
        super.processOpts();

        if (additionalProperties.containsKey(CodegenConstants.SOURCE_FOLDER)) {
            this.setSourceFolder((String) additionalProperties.get(CodegenConstants.SOURCE_FOLDER));
        } else {
            additionalProperties.put(CodegenConstants.SOURCE_FOLDER, sourceFolder);
        }

        if (additionalProperties.containsKey(CodegenConstants.PACKAGE_NAME)) {
            this.setPackageName((String) additionalProperties.get(CodegenConstants.PACKAGE_NAME));
            if (!additionalProperties.containsKey(CodegenConstants.MODEL_PACKAGE))
                this.setModelPackage(packageName + ".models");
            if (!additionalProperties.containsKey(CodegenConstants.API_PACKAGE))
                this.setApiPackage(packageName + ".apis");
        } else {
            additionalProperties.put(CodegenConstants.PACKAGE_NAME, packageName);
        }

        if (additionalProperties.containsKey(CodegenConstants.ARTIFACT_ID)) {
            this.setArtifactId((String) additionalProperties.get(CodegenConstants.ARTIFACT_ID));
        } else {
            additionalProperties.put(CodegenConstants.ARTIFACT_ID, artifactId);
        }

        if (additionalProperties.containsKey(CodegenConstants.GROUP_ID)) {
            this.setGroupId((String) additionalProperties.get(CodegenConstants.GROUP_ID));
        } else {
            additionalProperties.put(CodegenConstants.GROUP_ID, groupId);
        }

        if (additionalProperties.containsKey(CodegenConstants.ARTIFACT_VERSION)) {
            this.setArtifactVersion((String) additionalProperties.get(CodegenConstants.ARTIFACT_VERSION));
        } else {
            additionalProperties.put(CodegenConstants.ARTIFACT_VERSION, artifactVersion);
        }

        if (additionalProperties.containsKey(CodegenConstants.INVOKER_PACKAGE)) {
            LOGGER.warn(CodegenConstants.INVOKER_PACKAGE + " with " + this.getName() + " generator is ignored. Use " + CodegenConstants.PACKAGE_NAME + ".");
        }

        additionalProperties.put(CodegenConstants.API_PACKAGE, apiPackage());
        additionalProperties.put(CodegenConstants.MODEL_PACKAGE, modelPackage());

        additionalProperties.put("apiDocPath", apiDocPath);
        additionalProperties.put("modelDocPath", modelDocPath);

        supportingFiles.add(new SupportingFile("README.mustache", "", "README.md"));

        supportingFiles.add(new SupportingFile("AndroidManifest.xml.mustache", "src/main", "AndroidManifest.xml"));
        supportingFiles.add(new SupportingFile("build.gradle.mustache", "", "build.gradle"));
        supportingFiles.add(new SupportingFile("settings.gradle.mustache", "", "settings.gradle"));

        final String infrastructureFolder = (sourceFolder + File.separator + packageName + File.separator + "infrastructure").replace(".", "/");

        supportingFiles.add(new SupportingFile("infrastructure/RestModule.kt.mustache", infrastructureFolder, "RestModule.kt"));
//        supportingFiles.add(new SupportingFile("infrastructure/ApiAbstractions.kt.mustache", infrastructureFolder, "ApiAbstractions.kt"));
//        supportingFiles.add(new SupportingFile("infrastructure/ApiInfrastructureResponse.kt.mustache", infrastructureFolder, "ApiInfrastructureResponse.kt"));
//        supportingFiles.add(new SupportingFile("infrastructure/ApplicationDelegates.kt.mustache", infrastructureFolder, "ApplicationDelegates.kt"));
//        supportingFiles.add(new SupportingFile("infrastructure/RequestConfig.kt.mustache", infrastructureFolder, "RequestConfig.kt"));
//        supportingFiles.add(new SupportingFile("infrastructure/RequestMethod.kt.mustache", infrastructureFolder, "RequestMethod.kt"));
//        supportingFiles.add(new SupportingFile("infrastructure/ResponseExtensions.kt.mustache", infrastructureFolder, "ResponseExtensions.kt"));
//        supportingFiles.add(new SupportingFile("infrastructure/Serializer.kt.mustache", infrastructureFolder, "Serializer.kt"));
//        supportingFiles.add(new SupportingFile("infrastructure/Errors.kt.mustache", infrastructureFolder, "Errors.kt"));
    }

    @Override
    public String escapeUnsafeCharacters(String input) {
        return input.replace("*/", "*_/").replace("/*", "/_*");
    }

    @Override
    public String escapeQuotationMark(String input) {
        // remove " to avoid code injection
        return input.replace("\"", "");
    }

    @Override
    public String apiDocFileFolder() {
        return (outputFolder + "/" + apiDocPath).replace('/', File.separatorChar);
    }

    @Override
    public String apiFileFolder() {
        return outputFolder + File.separator + sourceFolder + File.separator + apiPackage().replace('.', File.separatorChar);
    }

    @Override
    public String modelDocFileFolder() {
        return (outputFolder + "/" + modelDocPath).replace('/', File.separatorChar);
    }


    @Override
    public String modelFileFolder() {
        return outputFolder + File.separator + sourceFolder + File.separator + modelPackage().replace('.', File.separatorChar);
    }

    @Override
    public String escapeReservedWord(String name) {
        return "_" + name;
    }

    /**
     * Output the proper model name (capitalized).
     * In case the name belongs to the TypeSystem it won't be renamed.
     *
     * @param name the name of the model
     * @return capitalized model name
     */
    @Override
    public String toModelName(String name) {
        if (name != null && !name.startsWith("kotlin.") && !name.startsWith("java.")) {
            return initialCaps(modelNamePrefix + name + modelNameSuffix);
        } else {
            return name;
        }
    }

    /**
     * returns the swagger type for the property
     *
     * @param p Swagger property object
     * @return string presentation of the type
     **/
    @Override
    public String getSwaggerType(Property p) {
        String swaggerType = super.getSwaggerType(p);
        String type;
        // This maps, for example, long -> kotlin.Long based on hashes in this type's constructor
        if (typeMapping.containsKey(swaggerType)) {
            type = typeMapping.get(swaggerType);
            if (languageSpecificPrimitives.contains(type)) {
                return toModelName(type);
            }
        } else {
            type = swaggerType;
        }
        return toModelName(type);
    }


    /**
     * Output the type declaration of the property
     *
     * @param p Swagger Property object
     * @return a string presentation of the property type
     */
    @Override
    public String getTypeDeclaration(Property p) {
        if (p instanceof ArrayProperty) {
            ArrayProperty ap = (ArrayProperty) p;
            Property inner = ap.getItems();
            return getSwaggerType(p) + "<" + getTypeDeclaration(inner) + ">";
        } else if (p instanceof MapProperty) {
            MapProperty mp = (MapProperty) p;
            Property inner = mp.getAdditionalProperties();

            // Maps will be keyed only by primitive Kotlin string
            return getSwaggerType(p) + "<String, " + getTypeDeclaration(inner) + ">";
        }
        return super.getTypeDeclaration(p);
    }

    /**
     * Check the type to see if it needs import the library/module/package
     *
     * @param type name of the type
     * @return true if the library/module/package of the corresponding type needs to be imported
     */
    @Override
    protected boolean needToImport(String type) {
        // provides extra protection against improperly trying to import language primitives and java types
        boolean imports = !type.startsWith("kotlin.") && !type.startsWith("java.") && !defaultIncludes.contains(type) && !languageSpecificPrimitives.contains(type);
        return imports;
    }

    /**
     * Return the fully-qualified "Model" name for import
     *
     * @param name the name of the "Model"
     * @return the fully-qualified "Model" name for import
     */
    @Override
    public String toModelImport(String name) {
        // toModelImport is called while processing operations, but DefaultCodegen doesn't
        // define imports correctly with fully qualified primitives and models as defined in this generator.
        if (needToImport(name)) {
            return super.toModelImport(name);
        }

        return name;
    }

    @Override
    public Map<String, Object> postProcessModels(Map<String, Object> objs) {
        return postProcessModelsEnum(super.postProcessModels(objs));
    }

    @Override
    public CodegenOperation fromOperation(String path, String httpMethod, Operation operation, Map<String, Model> definitions, Swagger swagger) {
        CodegenOperation codegenOperation = super.fromOperation(path, httpMethod, operation, definitions, swagger);


        // Attempt to order the parameters because our code generator needs that
        List<CodegenParameter> allParams = codegenOperation.allParams;

        Collections.sort(allParams, new Comparator<CodegenParameter>() {

            int HEADER = 0;
            int PATH = 1;
            int QUERY = 2;
            int BODY = 3;
            int FORM = 4;

            @Override
            public int compare(CodegenParameter o1, CodegenParameter o2) {
                int i1 = 0;
                int i2 = 0;

                if (o1.isHeaderParam) i1 = HEADER;
                if (o1.isPathParam) i1 = PATH;
                if (o1.isQueryParam) i1 = QUERY;
                if (o1.isBodyParam) i1 = BODY;
                if (o1.isFormParam) i1 = FORM;

                if (o2.isHeaderParam) i2 = HEADER;
                if (o2.isPathParam) i2 = PATH;
                if (o2.isQueryParam) i2 = QUERY;
                if (o2.isBodyParam) i2 = BODY;
                if (o2.isFormParam) i2 = FORM;

                return Integer.compare(i1, i2);
            }
        });

        return codegenOperation;
    }
}