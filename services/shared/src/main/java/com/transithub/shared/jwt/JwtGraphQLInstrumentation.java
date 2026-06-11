package com.transithub.shared.jwt;

import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.instrumentation.Instrumentation;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class JwtGraphQLInstrumentation implements Instrumentation {

    @Override
    public InstrumentationContext<graphql.execution.ExecutionResult> beginExecute(InstrumentationExecutionParameters parameters) {
        UUID userId = JwtUserIdHolder.get();
        if (userId != null) {
            parameters.getGraphQLContext().put("userId", userId);
        }
        return new SimpleInstrumentationContext<>();
    }
}
