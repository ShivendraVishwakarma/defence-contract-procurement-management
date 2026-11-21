export interface ContractSummary {
  id: number;
  contractNumber: string;
  title: string;
  vendorId: number;
  vendorName: string;
  statusCode: string;
  statusName: string;
  startDate: string;
  endDate: string;
  contractValue: number;
  summary: string;
}

export interface ContractFormOptions {
  vendors: VendorOption[];
  statuses: ContractStatusOption[];
}

export interface VendorOption {
  id: number;
  code: string;
  name: string;
}

export interface ContractStatusOption {
  code: string;
  name: string;
}

export interface ContractCreateRequest {
  contractNumber: string;
  title: string;
  vendorId: number;
  statusCode: string;
  startDate: string;
  endDate: string;
  contractValue: number;
  summary: string;
}

export type ContractUpdateRequest = Omit<ContractCreateRequest, 'statusCode'>;

export interface ContractStatusRequest {
  statusCode: string;
}
