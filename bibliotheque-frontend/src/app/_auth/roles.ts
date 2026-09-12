export const Roles = {
  administrateur: 'ADMINISTRATEUR',
  bibliothecaire: 'BIBLIOTHECAIRE',
  adherent: 'ADHERENT'
} as const;

export type RoleName = typeof Roles[keyof typeof Roles];
