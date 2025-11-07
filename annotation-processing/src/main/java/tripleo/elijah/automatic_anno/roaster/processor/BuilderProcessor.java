package tripleo.elijah.automatic_anno.roaster.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("tripleo.elijah.automatic_anno.BuilderProperty")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class BuilderProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (TypeElement annotation : annotations) {
			Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

			Map<Boolean, List<Element>> annotatedMethods = annotatedElements.stream().collect(Collectors.partitioningBy(element -> ((ExecutableType) element.asType()).getParameterTypes().size() == 1 && element.getSimpleName().toString().startsWith("set")));

			List<Element> setters      = annotatedMethods.get(true);
			List<Element> otherMethods = annotatedMethods.get(false);

			otherMethods.forEach(element -> processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@BuilderProperty must be applied to a setXxx method with a single argument", element));

			if (setters.isEmpty()) {
				continue;
			}

			String className = ((TypeElement) setters.get(0).getEnclosingElement()).getQualifiedName().toString();

			Map<String, String> setterMap = setters.stream().collect(Collectors.toMap(setter -> setter.getSimpleName().toString(), setter -> ((ExecutableType) setter.asType()).getParameterTypes().get(0).toString()));

			Exception e = new RoasterPortion(processingEnv).getResult(className, setterMap);
			if (e != null) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "" + e.getMessage());
			}
		}

		return true;
	}
}

//
//
//
