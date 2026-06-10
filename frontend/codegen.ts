import type { CodegenConfig } from '@graphql-codegen/cli';

const config: CodegenConfig = {
  schema: '../backend/src/main/resources/graphql/schema.graphqls',
  documents: 'src/app/core/graphql/**/*.graphql',
  generates: {
    'src/app/core/graphql/generated.ts': {
      plugins: ['typescript', 'typescript-operations', 'typed-document-node'],
      config: {
        avoidOptionals: true,
        maybeValue: 'T | null',
        inputMaybeValue: 'T | null',
        enumsAsTypes: true,
        preResolveTypes: false,
        documentMode: 'documentNode',
      },
    },
  },
};

export default config;
