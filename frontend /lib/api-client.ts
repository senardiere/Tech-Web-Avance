const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:5089/api';

export interface ApiResponse<T> {
  data?: T;
  error?: string;
  message?: string;
}

export interface User {
  id: number;
  login: string;
  nom: string;
  prenom: string;
  email: string;
  role: string;
  type: string;
  actif: boolean;
}

export interface Patient {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  adresse: string;
  dateNaissance: string;
  statut: string;
  dateCreation: string;
  medecinTraitant?: {
    id: number;
    nom: string;
    prenom: string;
  };
}

export interface Medecin {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  login: string;
  numeroLicence: string;
  cabinet: string;
  actif: boolean;
  specialite: {
    id: number;
    nom: string;
  };
}

export interface Specialite {
  id: number;
  nom: string;
  code: string;
  description: string;
  actif: boolean;
}

export interface RendezVous {
  id: number;
  dateHeure: string;
  duree: number;
  statut: string;
  motif: string;
  patient: {
    id: number;
    nom: string;
    prenom: string;
  };
  medecin: {
    id: number;
    nom: string;
    prenom: string;
  };
}

export interface Consultation {
  id: number;
  patientId: number;
  patientNom: string;
  patientPrenom: string;
  medecinId: number;
  medecinNom: string;
  medecinPrenom: string;
  dateConsultation: string;
  statut: string;
  motif: string;
  diagnostic: string;
  prescription: string;
  montant: number;
}

class ApiClient {
  private baseUrl: string;

  constructor(baseUrl: string = API_BASE_URL) {
    this.baseUrl = baseUrl;
  }

  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> {
    const url = `${this.baseUrl}${endpoint}`;
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers,
    };

    const response = await fetch(url, {
      ...options,
      headers,
      credentials: 'include',
    });

    if (!response.ok) {
      const error = await response.text();
      throw new Error(error || `API Error: ${response.status}`);
    }

    if (response.status === 204) {
      return undefined as T;
    }

    return response.json();
  }

  // Auth endpoints
  async login(login: string, motDePasse: string): Promise<User> {
    return this.request<User>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ login, motDePasse }),
    });
  }

  async logout(): Promise<void> {
    return this.request<void>('/auth/logout', {
      method: 'POST',
    });
  }

  async getCurrentUser(): Promise<User | null> {
    try {
      return await this.request<User>('/auth/current-user', {
        method: 'GET',
      });
    } catch {
      return null;
    }
  }

  async isAuthenticated(): Promise<boolean> {
    try {
      const response = await this.request<{ authenticated: boolean }>('/auth/is-authenticated', {
        method: 'GET',
      });
      return response.authenticated;
    } catch {
      return false;
    }
  }

  // Patients endpoints
  async getPatients(): Promise<Patient[]> {
    return this.request<Patient[]>('/patients', { method: 'GET' });
  }

  async getPatientById(id: number): Promise<Patient> {
    return this.request<Patient>(`/patients/${id}`, { method: 'GET' });
  }

  async searchPatients(nom: string): Promise<Patient[]> {
    return this.request<Patient[]>(`/patients/search?nom=${nom}`, { method: 'GET' });
  }

  async createPatient(data: Partial<Patient>): Promise<Patient> {
    return this.request<Patient>('/patients', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async updatePatient(id: number, data: Partial<Patient>): Promise<Patient> {
    return this.request<Patient>(`/patients/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async deletePatient(id: number): Promise<void> {
    return this.request<void>(`/patients/${id}`, { method: 'DELETE' });
  }

  async getLastPatients(limit: number = 10): Promise<Patient[]> {
    return this.request<Patient[]>(`/patients/derniers?limit=${limit}`, { method: 'GET' });
  }

  // Medecins endpoints
  async getMedecins(): Promise<Medecin[]> {
    return this.request<Medecin[]>('/medecins', { method: 'GET' });
  }

  async getMedecinById(id: number): Promise<Medecin> {
    return this.request<Medecin>(`/medecins/${id}`, { method: 'GET' });
  }

  async getMedecinsBySpecialite(specialiteId: number): Promise<Medecin[]> {
    return this.request<Medecin[]>(`/medecins/specialite/${specialiteId}`, { method: 'GET' });
  }

  async createMedecin(data: Partial<Medecin> & { motDePasse: string }): Promise<Medecin> {
    return this.request<Medecin>('/medecins', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async updateMedecin(id: number, data: Partial<Medecin>): Promise<Medecin> {
    return this.request<Medecin>(`/medecins/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async deleteMedecin(id: number): Promise<void> {
    return this.request<void>(`/medecins/${id}`, { method: 'DELETE' });
  }

  // Specialites endpoints
  async getSpecialites(): Promise<Specialite[]> {
    return this.request<Specialite[]>('/specialites', { method: 'GET' });
  }

  async getSpecialiteById(id: number): Promise<Specialite> {
    return this.request<Specialite>(`/specialites/${id}`, { method: 'GET' });
  }

  async createSpecialite(data: Partial<Specialite>): Promise<Specialite> {
    return this.request<Specialite>('/specialites', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async updateSpecialite(id: number, data: Partial<Specialite>): Promise<Specialite> {
    return this.request<Specialite>(`/specialites/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async deleteSpecialite(id: number): Promise<void> {
    return this.request<void>(`/specialites/${id}`, { method: 'DELETE' });
  }

  // RendezVous endpoints
  async getRendezVous(): Promise<RendezVous[]> {
    return this.request<RendezVous[]>('/rendezvous', { method: 'GET' });
  }

  async getRendezVousById(id: number): Promise<RendezVous> {
    return this.request<RendezVous>(`/rendezvous/${id}`, { method: 'GET' });
  }

  async getRendezVousByMedecin(medecinId: number): Promise<RendezVous[]> {
    return this.request<RendezVous[]>(`/rendezvous/medecin/${medecinId}`, { method: 'GET' });
  }

  async getRendezVousByPatient(patientId: number): Promise<RendezVous[]> {
    return this.request<RendezVous[]>(`/rendezvous/patient/${patientId}`, { method: 'GET' });
  }

  async getRendezVousDuJour(): Promise<RendezVous[]> {
    return this.request<RendezVous[]>('/rendezvous/du-jour', { method: 'GET' });
  }

  async createRendezVous(data: Partial<RendezVous>): Promise<RendezVous> {
    return this.request<RendezVous>('/rendezvous', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async updateRendezVous(id: number, data: Partial<RendezVous>): Promise<RendezVous> {
    return this.request<RendezVous>(`/rendezvous/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async validerRendezVous(id: number): Promise<void> {
    return this.request<void>(`/rendezvous/${id}/valider`, {
      method: 'PATCH',
    });
  }

  async annulerRendezVous(id: number, motif: string): Promise<void> {
    return this.request<void>(`/rendezvous/${id}/annuler?motif=${motif}`, {
      method: 'PATCH',
    });
  }

  async deleteRendezVous(id: number): Promise<void> {
    return this.request<void>(`/rendezvous/${id}`, { method: 'DELETE' });
  }

  // Consultations endpoints
  async getConsultations(): Promise<Consultation[]> {
    return this.request<Consultation[]>('/consultations', { method: 'GET' });
  }

  async getConsultationById(id: number): Promise<Consultation> {
    return this.request<Consultation>(`/consultations/${id}`, { method: 'GET' });
  }

  async getConsultationsByMedecin(medecinId: number): Promise<Consultation[]> {
    return this.request<Consultation[]>(`/consultations/medecin/${medecinId}`, { method: 'GET' });
  }

  async getConsultationsByPatient(patientId: number): Promise<Consultation[]> {
    return this.request<Consultation[]>(`/consultations/patient/${patientId}`, { method: 'GET' });
  }

  async createConsultation(data: Partial<Consultation>): Promise<Consultation> {
    return this.request<Consultation>('/consultations', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async updateConsultation(id: number, data: Partial<Consultation>): Promise<Consultation> {
    return this.request<Consultation>(`/consultations/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async terminerConsultation(id: number): Promise<void> {
    return this.request<void>(`/consultations/${id}/terminer`, {
      method: 'PATCH',
    });
  }

  async deleteConsultation(id: number): Promise<void> {
    return this.request<void>(`/consultations/${id}`, { method: 'DELETE' });
  }
}

export const apiClient = new ApiClient();
