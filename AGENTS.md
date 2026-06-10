# Agent Instructions

## Context loading

Read docs/plan.md only for the current phase/task.

Read docs/schema.md only when touching backend data model, GraphQL schema, migrations, entities, repositories, or services.

Read docs/development.md when running or changing commands, tests, tooling, or setup.

Do not read unrelated docs or generated files unless necessary.

Do not inspect or summarize generated files like frontend/src/app/core/graphql/generated.ts unless debugging codegen. If GraphQL operations change, run codegen and report that generated types changed.

## Scope control

Only complete the requested phase or task.

Do not start the next phase without approval.

Do not create GitHub Actions workflows unless explicitly requested.

Do not add new dependencies unless necessary for the requested task. Explain why any dependency is needed.

## Generated files

Do not edit generated GraphQL files manually.

Regenerate generated GraphQL files with codegen.

## Verification

Run only the documented format/check/test commands relevant to the files changed.

Do not run frontend npm test until ChromeHeadless or CHROME_BIN is configured. Use npm run lint and npm run build for frontend verification unless frontend test execution has been fixed.

## Reporting

Keep final reports brief.

Do not paste full logs unless a command failed.

Report only:
- files changed
- commands run
- pass/fail status
- assumptions or blockers
