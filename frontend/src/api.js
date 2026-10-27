const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options
  });
  if (!response.ok) {
    const message = await response.text().catch(() => '');
    throw new Error(message || `Request failed (${response.status})`);
  }
  if (response.status === 204) return null;
  return response.json();
}

export const api = {
  health: () => request('/health'),
  dashboard: () => request('/dashboard'),
  readings: () => request('/readings'),
  leaks: () => request('/leaks'),
  tanks: () => request('/tanks'),
  consumption: () => request('/analytics/consumption'),
  savings: () => request('/analytics/savings'),
  startSimulation: () => request('/simulation/start', { method: 'POST' }),
  stopSimulation: () => request('/simulation/stop', { method: 'POST' }),
  simulationStatus: () => request('/simulation/status'),
  detectLeaks: () => request('/leaks/detect', { method: 'POST' }),
  createReading: (payload) => request('/readings', { method: 'POST', body: JSON.stringify(payload) }),
  repairLeak: (id, postRepairFlowLpm) => request(`/leaks/${id}/repair`, {
    method: 'POST', body: JSON.stringify({ postRepairFlowLpm })
  }),
  tankPrediction: (id) => request(`/tanks/${id}/prediction`)
};
