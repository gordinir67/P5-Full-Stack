import { TokenService } from './token.service';

describe('TokenService', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('reads initial token from localStorage', () => {
    localStorage.setItem('mdd_token', 'stored-token');

    const service = new TokenService();

    expect(service.getToken()).toBe('stored-token');
  });

  it('stores and emits token', () => {
    const service = new TokenService();
    const emissions: Array<string | null> = [];
    service.token$.subscribe((value) => emissions.push(value));

    service.setToken('abc');

    expect(localStorage.getItem('mdd_token')).toBe('abc');
    expect(service.getToken()).toBe('abc');
    expect(emissions.at(-1)).toBe('abc');
  });

  it('clears token from storage and subject', () => {
    const service = new TokenService();
    service.setToken('abc');

    service.clearToken();

    expect(localStorage.getItem('mdd_token')).toBeNull();
    expect(service.getToken()).toBeNull();
  });

  it('returns null when localStorage access throws', () => {
    const getItemSpy = jest.spyOn(Storage.prototype, 'getItem').mockImplementation(() => {
      throw new Error('storage disabled');
    });

    const service = new TokenService();

    expect(service.getToken()).toBeNull();
    getItemSpy.mockRestore();
  });
});
