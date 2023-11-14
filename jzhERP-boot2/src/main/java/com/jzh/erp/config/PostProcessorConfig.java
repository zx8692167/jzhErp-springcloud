package com.jzh.erp.config;

//@Configuration
public class PostProcessorConfig {} /*extends CommonAnnotationBeanPostProcessor {
    AutowiredAnnotationBeanPostProcessor


private class LegacyResourceElement extends CommonAnnotationBeanPostProcessor.LookupElement {
    private final boolean lazyLookup;

    public LegacyResourceElement(Member member, AnnotatedElement ae, @Nullable PropertyDescriptor pd) {
        super(member, pd);
        jakarta.annotation.Resource resource = (jakarta.annotation.Resource)ae.getAnnotation(jakarta.annotation.Resource.class);
        String resourceName = resource.name();
        Class<?> resourceType = resource.type();
        this.isDefaultName = !StringUtils.hasLength(resourceName);
        if (this.isDefaultName) {
            resourceName = this.member.getName();
            if (this.member instanceof Method && resourceName.startsWith("set") && resourceName.length() > 3) {
                resourceName = StringUtils.uncapitalizeAsProperty(resourceName.substring(3));
            }
        } else if (embeddedValueResolver != null) {
            resourceName = super.embeddedValueResolver.resolveStringValue(resourceName);
        }

        if (Object.class != resourceType) {
            this.checkResourceType(resourceType);
        } else {
            resourceType = this.getResourceType();
        }

        this.name = resourceName != null ? resourceName : "";
        this.lookupType = resourceType;
        String lookupValue = resource.lookup();
        this.mappedName = StringUtils.hasLength(lookupValue) ? lookupValue : resource.mappedName();
        Lazy lazy = (Lazy)ae.getAnnotation(Lazy.class);
        this.lazyLookup = lazy != null && lazy.value();
    }

    protected Object getResourceToInject(Object target, @Nullable String requestingBeanName) {
        return this.lazyLookup ? CommonAnnotationBeanPostProcessor.this.buildLazyResourceProxy(this, requestingBeanName) : CommonAnnotationBeanPostProcessor.this.getResource(this, requestingBeanName);
    }
}
}*/