import { HttpClientTestingModule } from '@angular/common/http/testing';

import { AppModule } from '../app.module';

/**
 * Charge le contexte applicatif réel tout en neutralisant les appels HTTP.
 * Les anciens tests générés ne déclaraient pas les dépendances de routage,
 * formulaires et services utilisées par les composants.
 */
export const APP_TEST_IMPORTS = [AppModule, HttpClientTestingModule];
