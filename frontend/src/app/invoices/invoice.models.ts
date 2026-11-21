export interface Invoice {
  id: number;
  invoiceNumber: string;
  contractId: number;
  contractNumber: string;
  statusCode: string;
  statusName: string;
  invoiceDate: string;
  dueDate: string;
  totalAmount: number;
}
export interface InvoiceOptions {
  contracts: { id: number; contractNumber: string; title: string }[];
  invoiceStatuses: { code: string; name: string }[];
  paymentStatuses: { code: string; name: string }[];
}
export interface InvoiceRequest {
  invoiceNumber: string; contractId: number; statusCode: string; invoiceDate: string; dueDate: string; totalAmount: number;
}
export interface PaymentRequest {
  paymentReference: string; statusCode: string; paymentDate: string; amount: number; paymentMethod: string;
}
