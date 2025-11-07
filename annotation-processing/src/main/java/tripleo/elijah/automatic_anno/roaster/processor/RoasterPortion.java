package tripleo.elijah.automatic_anno.roaster.processor;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class RoasterPortion {
	private final ProcessingEnvironment processingEnv1;
	private       String                builderClassName;

	public RoasterPortion(final ProcessingEnvironment aProcessingEnv) {
		processingEnv1 = aProcessingEnv;
	}

	public Exception getResult(final String aClassName, final Map<String, String> aSetterMap) {
		assert processingEnv1 != null;
		try {
			final JavaClassSource javaClass = _part3(aClassName, aSetterMap);
			assert javaClass != null;
			assert builderClassName != null;
			final JavaFileObject builderFile = processingEnv1.getFiler().createSourceFile(builderClassName);
			try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
				out.print(javaClass.toString());
			}
		} catch (IOException aE) {
			// throw new RuntimeException(aE);
			return aE;
		}
		return null;
	}

	private JavaClassSource _part3(final String aClassName, final Map<String, String> aSetterMap) throws IOException {
		String packageName = null;
		int    lastDot     = aClassName.lastIndexOf('.');
		if (lastDot > 0) {
			packageName = aClassName.substring(0, lastDot);
		}

		String simpleClassName  = aClassName.substring(lastDot + 1);
		this.builderClassName = aClassName + "Builder";
		String builderSimpleClassName = this.builderClassName.substring(lastDot + 1);

		final JavaClassSource javaClass = Roaster.create(JavaClassSource.class);
		javaClass
			.setName(builderSimpleClassName)
			.setPackage(packageName)
			.setPublic();

		javaClass.addField()
			.setName("object")
			.setType(simpleClassName)
			.setPrivate()
			.setFinal(true)
			.setLiteralInitializer("new " + simpleClassName + "();");

		javaClass.addMethod()
			.setName("build")
			.setReturnType(simpleClassName)
			.setPublic()
			.setBody("        return object;");

		aSetterMap.entrySet().forEach(setter -> {
			String methodName   = setter.getKey();
			String argumentType = setter.getValue();

			MethodSource<JavaClassSource> m = javaClass.addMethod()
				.setName(methodName)
				.setPublic()
				.setReturnType(builderSimpleClassName);
			m.addParameter(argumentType, "value");
			m.setBody("this.object." + methodName + " (value);" + "return this;");
		});

		return javaClass;
	}
}
